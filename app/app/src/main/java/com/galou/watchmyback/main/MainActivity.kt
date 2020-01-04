package com.galou.watchmyback.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.firebase.ui.auth.AuthUI
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.Trip
import com.galou.watchmyback.data.entity.TripUpdateFrequency
import com.galou.watchmyback.databinding.ActivityMainBinding
import com.galou.watchmyback.databinding.HeaderNavViewBinding
import com.galou.watchmyback.detailsTrip.DetailsTripActivity
import com.galou.watchmyback.profile.ProfileActivity
import com.galou.watchmyback.settings.SettingsActivity
import com.galou.watchmyback.utils.*
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding

    private var navController: NavController? = null

    private lateinit var authFirebase: FirebaseAuth
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    private lateinit var notificationManager: NotificationManager

    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationLateChannel()
        createNotificationBackLateChannel()
        configureBinding()
        setupObserverViewModel()
        configureFirebase()
        configureToolbar()
        configureBottomNavigation()
        configureNavigationView()
        setupOpenMyTripActivityObserver()
        setupLateNotificationObserver()
        setupBackHomeNotificationObserver()
        setupConfigureCheckUpObserver()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_SIGN_IN -> viewModel.handleSignIngActivityResult(resultCode, data, authFirebase.currentUser)
            RC_PROFILE ->  viewModel.handleResultAfterProfileActivityClosed(resultCode)
            RC_SETTINGS -> viewModel.handleResultSettingsActivity(resultCode)
        }
    }

    private fun configureBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
    }

    //-------------------------
    //  VIEW MODEL CONNECTION
    //-------------------------

    private fun setupObserverViewModel(){
        setupSnackBar()
        setupOpenSignInActivity()
    }

    private fun setupSnackBar(){
        val rooView = binding.mainActivityContainer
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun setupOpenSignInActivity(){
        viewModel.openSignInActivityEvent.observe(this, EventObserver { openSignInActivity()})
    }

    private fun setupOpenMyTripActivityObserver(){
        viewModel.openMyTripActivityLD.observe(this, EventObserver { openMyTripActivity() })
    }

    private fun setupLateNotificationObserver(){
        viewModel.enableLateNotificationLD.observe(this, EventObserver { configureLateNotification(it) })
    }

    private fun setupBackHomeNotificationObserver(){
        viewModel.enableBackHomeNotificationLD.observe(this, EventObserver { configureBackHomeNotification(it) })
    }

    private fun setupConfigureCheckUpObserver(){
        viewModel.configureTripCheckUpLD.observe(this, EventObserver { configureCheckUpWorkManager(it) })
    }

    //-------------------------
    //  CONFIGURE UI ELEMENT
    //-------------------------

    private fun configureToolbar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        drawerLayout = binding.mainActivityDrawerLayout
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.open_nav_drawer,
            R.string.close_nav_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun configureBottomNavigation(){
        val bottomNavigation = binding.mainActivityBottomNav
        navController = Navigation.findNavController(this,
            R.id.main_activity_nav_host
        )
        NavigationUI.setupWithNavController(bottomNavigation, navController!!)

    }

    private fun configureNavigationView(){
        val navigationView = binding.mainActivityNavigationView
        val navViewBinding = DataBindingUtil.inflate<HeaderNavViewBinding>(
            layoutInflater, R.layout.header_nav_view, navigationView, false
        )
        navViewBinding.viewmodel = viewModel
        navViewBinding.lifecycleOwner = this
        binding.mainActivityNavigationView.addHeaderView(navViewBinding.root)
        navigationView.setNavigationItemSelectedListener(this)
    }

    //-------------------------
    //  NAVIGATION VIEW ACTIONS
    //-------------------------

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = false
        when(item.itemId){
            R.id.nav_view_menu_settings -> openSettingsActivity()
            R.id.nav_view_menu_my_profile -> openProfileActivity()
            R.id.nav_view_menu_my_trip -> viewModel.showMyTripActivity()
            R.id.nav_view_menu_logout -> logOutAction()
        }
        drawerLayout.closeDrawer(GravityCompat.START)

        return true

    }

    private fun openSettingsActivity(){
        navController?.saveState()
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun openProfileActivity(){
        val intent = Intent(this, ProfileActivity::class.java)
        startActivityForResult(intent, RC_PROFILE)
    }

    private fun openMyTripActivity(){
        val intent = Intent(this, DetailsTripActivity::class.java)
        startActivity(intent)

    }

    private fun logOutAction(){
        viewModel.logOutUser(this)

    }

    //-------------------------
    //  FIREBASE AUTH ACTIONS
    //-------------------------

    private fun configureFirebase(){
        authFirebase = FirebaseAuth.getInstance()
        viewModel.checkIfUserIsConnected(authFirebase.currentUser)

    }

    private fun openSignInActivity(){
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    //-------------------------
    //  NOIFICATIONS
    //-------------------------

    private fun createNotificationLateChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(R.string.channel_late_name)
            val descriptionText = getString(R.string.channel_late_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_LATE_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationBackLateChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(R.string.channel_back_name)
            val descriptionText = getString(R.string.channel_back_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_BACK_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    //-----------------------
    // TRIP WATCHING NOTIFICATION
    //-----------------------

    private fun configureLateNotification(userId: String){
        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                LATE_NOTIFICATION_WORKER_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                createLateNotificationWorker(userId)
            )
    }

    private fun configureBackHomeNotification(userId: String){
        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                BACK_NOTIFICATION_WORKER_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                createBackNotificationWorker(userId)
            )
    }

    private fun configureCheckUpWorkManager(trip: Trip){
        if (trip.updateFrequency != TripUpdateFrequency.NEVER){
            WorkManager.getInstance(applicationContext)
                .enqueueUniquePeriodicWork(
                    CHECK_UP_WORKER_TAG,
                    ExistingPeriodicWorkPolicy.KEEP,
                    createCheckUpWorker(trip)
                )
        }
    }



}
