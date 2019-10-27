package com.galou.watchmyback.profileActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.databinding.ActivityProfileBinding
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileActivity : AppCompatActivity() {

    private val viewModel: ProfileViewModel by viewModel()

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureBinding()
        setupObserverViewModel()
        configureToolbar()
    }

    private fun configureBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
    }

    //-------------------------
    //  VIEW MODEL CONNECTION
    //-------------------------

    private fun setupObserverViewModel(){
        setupSnackBar()
        setupDataSaved()
    }

    private fun setupSnackBar(){
        val rooView = binding.activityProfileContainer
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun setupDataSaved(){
        viewModel.dataSaved.observe(this, EventObserver{ finish() })
    }

    //-------------------------
    //  CONFIGURE UI
    //-------------------------
    private fun configureToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.validate_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.validate_menu_validate -> viewModel.updateUserInformation()
        }

        return super.onOptionsItemSelected(item)
    }
}
