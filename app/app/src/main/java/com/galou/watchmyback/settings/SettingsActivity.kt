package com.galou.watchmyback.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.databinding.ActivitySettingsBinding
import com.galou.watchmyback.utils.RESULT_DELETED
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

    private fun configureBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
    }

    private fun configureEmergencyNumberListener(){
        val numberField = binding.settingsViewEmergencyNumber
        numberField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = viewModel.updateUserPreferences()
        })
    }

    private fun setupObserverViewModel(){
        setupSnackBar()
        setupDataDeleted()
    }

    private fun setupSnackBar(){
        val rooView = binding.activitySettingsContainer
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun setupDataDeleted(){
        viewModel.dataDeleted.observe(this, EventObserver { closeActivityAndEmitDeleted() })
    }

    private fun closeActivityAndEmitDeleted(){
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    setResult(RESULT_DELETED)
                    finish()
                } else {
                    viewModel.errorDeletion()
                }

            }

    }
}
