package com.galou.watchmyback.profileActivity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.databinding.ActivityProfileBinding
import com.galou.watchmyback.utils.RC_LIBRARY_PICK
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.galou.watchmyback.utils.intentSinglePicture
import com.galou.watchmyback.utils.requestPermissionStorage
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_LIBRARY_PICK -> viewModel.fetchPicturePickedByUser(resultCode, data?.data)
        }
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
        setupOpenPhotoDialog()
    }

    private fun setupSnackBar(){
        val rooView = binding.activityProfileContainer
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun setupDataSaved(){
        viewModel.dataSaved.observe(this, EventObserver{ modificationsSaved() })
    }

    private fun setupOpenPhotoDialog(){
        viewModel.openPhotoLibrary.observe(this, EventObserver{ openPhotoLibrary() })
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

    //-------------------------
    //  PHOTO ACTIONS
    //-------------------------

    private fun openPhotoLibrary(){
        requestPermissionStorage(this)
        if(requestPermissionStorage(this)) {
            startActivityForResult(intentSinglePicture(), RC_LIBRARY_PICK)
        }

    }

    private fun modificationsSaved(){
        setResult(Activity.RESULT_OK)
        finish()
    }

}
