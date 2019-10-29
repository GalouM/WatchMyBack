package com.galou.watchmyback.mainActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.firebase.ui.auth.AuthUI
import com.galou.watchmyback.*
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.databinding.ActivityMainBinding
import com.galou.watchmyback.databinding.HeaderNavViewBinding
import com.galou.watchmyback.profileActivity.ProfileActivity
import com.galou.watchmyback.utils.RC_PROFILE
import com.galou.watchmyback.utils.RC_SIGN_IN
import com.galou.watchmyback.utils.displayData
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding

    private lateinit var authFirebase: FirebaseAuth
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureBinding()
        setupObserverViewModel()
        configureFirebase()
        configureToolbar()
        configureBottomNavigation()
        configureNavigationView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_SIGN_IN -> viewModel.handleSignIngActivityResult(resultCode, data, authFirebase.currentUser)
            RC_PROFILE ->  viewModel.handleResultAfterProfileActivityClosed(resultCode)
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

        viewModel.userLD.observe(this, Observer { displayData("$it") })
    }

    private fun setupSnackBar(){
        val rooView = binding.mainActivityContainer
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun setupOpenSignInActivity(){
        viewModel.openSignInActivityEvent.observe(this, EventObserver { openSignInActivity()})
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
        val navController = Navigation.findNavController(this,
            R.id.main_activity_nav_host
        )
        NavigationUI.setupWithNavController(bottomNavigation, navController)

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
        displayData(item.itemId.toString())
        when(item.itemId){
            R.id.nav_view_menu_settings -> openSettingsActivity()
            R.id.nav_view_menu_my_profile -> openProfileActivity()
            R.id.nav_view_menu_my_trip -> openMyTripActivity()
            R.id.nav_view_menu_logout -> logOutAction()
        }
        drawerLayout.closeDrawer(GravityCompat.START)

        return true

    }

    private fun openSettingsActivity(){
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



}
