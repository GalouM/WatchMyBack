package com.galou.watchmyback.detailsTrip

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.detailsPoint.DetailsPointActivity
import com.galou.watchmyback.utils.TRIP_ID
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.viewmodel.ext.android.viewModel

class DetailsTripActivity : AppCompatActivity() {

    private val viewModel: DetailsTripViewModel by viewModel()

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_trip)

        configureToolbar()
        configureBottomNavigation()
        fetchTripInformation()
        setupViewModelObserver()

    }

    private fun configureToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun configureBottomNavigation(){
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.details_trip_activity_bottom_nav)
        navController = Navigation.findNavController(this,
            R.id.details_trip_activity_nav_host
        )
        NavigationUI.setupWithNavController(bottomNavigation, navController!!)

    }

    private fun fetchTripInformation(){
        viewModel.fetchTripInfo(intent.getStringExtra(TRIP_ID))
    }

    private fun setupViewModelObserver(){
        setupClickPointObserver()

    }

    private fun setupClickPointObserver(){
        viewModel.showPointDetailsLD.observe(this, EventObserver { showPointDetails() })
    }

    private fun showPointDetails(){
        with(Intent(this, DetailsPointActivity::class.java)){
            startActivity(this)
        }
    }
}
