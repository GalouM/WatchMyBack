package com.galou.watchmyback.settings

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.firebase.ui.auth.AuthUI
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.databinding.ActivitySettingsBinding
import com.galou.watchmyback.utils.*
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureBinding()
        configureToolbar()
        configureClickListener()
        configureEmergencyNumberListener()
        setupObserverViewModel()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun configureBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
    }

    private fun configureClickListener(){
        binding.settingsViewNotificationBackSwitch.apply {
            setOnClickListener { viewModel.clickNotificationBackHome(isChecked) }
        }
        binding.settingsViewNotificationLateSwitch.apply {
            setOnClickListener { viewModel.clickNotificationLate(isChecked) }
        }

    }

    private fun configureEmergencyNumberListener(){
        binding.settingsViewEmergencyNumber.doAfterTextChanged { searchTerm ->
            val currentTextLength = searchTerm?.length
            Handler().postDelayed({
                if (currentTextLength == searchTerm?.length) viewModel.updateUserPreferences()
            }, 2000)
        }
    }

    private fun setupObserverViewModel(){
        setupSnackBar()
        setupDataDeleted()
        setupLateNotificationEnableObserver()
        setupBackHomeNotificationEnableObserver()
    }

    private fun setupSnackBar(){
        val rooView = binding.activitySettingsContainer
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun setupDataDeleted(){
        viewModel.dataDeleted.observe(this, EventObserver { closeActivityAndEmitDeleted() })
    }

    private fun setupLateNotificationEnableObserver(){
        viewModel.enableLateNotificationLD.observe(this, EventObserver{ turnOnNotificationLate(it) })

    }

    private fun setupBackHomeNotificationEnableObserver(){
        viewModel.enableBackHomeNotificationLD.observe(this, EventObserver { turnOnNotificationBack(it) })

    }

    private fun setupLateNotificationDisableObserver(){
        viewModel.disableLateNotificationLD.observe(this, EventObserver { turnOffNotificationLate() })
    }

    private fun setupBackHomeNotificationDisableObserver(){
        viewModel.disableBackHomeNotificationLD.observe(this, EventObserver { turnOffNotificationBack() })
    }

    private fun closeActivityAndEmitDeleted(){
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    setResult(RESULT_ACCOUNT_DELETED)
                    finish()
                } else {
                    viewModel.errorDeletion()
                }

            }

    }

    private fun turnOnNotificationLate(userId: String){
        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                LATE_NOTIFICATION_WORKER_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                createLateNotificationWorker(userId)
            )
    }

    private fun turnOffNotificationLate(){
        WorkManager.getInstance(applicationContext).cancelAllWorkByTag(LATE_NOTIFICATION_WORKER_TAG)
    }

    private fun turnOnNotificationBack(userId: String){
        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                BACK_NOTIFICATION_WORKER_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                createBackNotificationWorker(userId)
            )
    }

    private fun turnOffNotificationBack(){
        WorkManager.getInstance(applicationContext).cancelAllWorkByTag(BACK_NOTIFICATION_WORKER_TAG)
    }
}
