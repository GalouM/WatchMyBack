package com.galou.watchmyback

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.galou.watchmyback.utils.displayData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureToolbar()
        configureBottomNavigation()
        configureNavigationView()
    }

    //-------------------------
    //  CONFIGURE UI ELEMENT
    //-------------------------

    private fun configureToolbar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        drawerLayout = findViewById(R.id.main_activity_drawer_layout)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.open_nav_drawer, R.string.close_nav_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun configureBottomNavigation(){
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.main_activity_bottom_nav)
        val navController = Navigation.findNavController(this, R.id.main_activity_nav_host)
        NavigationUI.setupWithNavController(bottomNavigation, navController)

    }

    private fun configureNavigationView(){
        val navigationView = findViewById<NavigationView>(R.id.main_activity_navigation_view)
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
        startActivity(intent)
    }

    private fun openMyTripActivity(){
        val intent = Intent(this, DetailsTripActivity::class.java)
        startActivity(intent)

    }

    private fun logOutAction(){

    }
}
