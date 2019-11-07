package com.galou.watchmyback.settings

import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.data.repository.UserRepositoryImpl
import com.galou.watchmyback.profile.ProfileActivity
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.extension.onClickTimeDisplay
import com.galou.watchmyback.utils.extension.onClickUnitSystem
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * [ViewModel] of [SettingsActivity]
 *
 * Inherit from [BaseViewModel]
 *
 * @see SettingsActivity
 *
 *
 * @property userRepository [UserRepositoryImpl] reference
 */
open class SettingsViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _dataDeleted = MutableLiveData<Event<Unit>>()
    val dataDeleted: LiveData<Event<Unit>> = _dataDeleted

    private val _dataSaved = MutableLiveData<Event<Unit>>()
    val dataSaved: LiveData<Event<Unit>> = _dataSaved

    val preferencesLD = MutableLiveData<UserPreferences>()

    private var updatePreferencesJob: Job? = null
    private var deleteUserJob: Job? = null

    init {
        _dataLoading.value = true
        fetchPreferences()
    }

    /**
     * Update and save the new user's preferences
     *
     * @see UserRepository.updateUserPreferences
     *
     */
    fun updateUserPreferences(){
        _dataLoading.value = true
        if(updatePreferencesJob?.isActive == true) updatePreferencesJob?.cancel()
        updatePreferencesJob = viewModelScope.launch {
            when(userRepository.updateUserPreferences(preferencesLD.value!!)){
                is Result.Success -> _dataSaved.value = Event(Unit)
                is Result.Error -> showSnackBarMessage(R.string.fail_not_saved)
                is Result.Canceled -> showSnackBarMessage(R.string.canceled)
            }
            _dataLoading.value = false
        }


    }

    /**
     * Delete the user's information and emit an [Event] to signal that the data have been deleted
     *
     * @see UserRepository.deleteUser
     *
     */
    fun deleteUserData(){
        _dataLoading.value = true
        if(deleteUserJob?.isActive == true) deleteUserJob?.cancel()
        deleteUserJob = viewModelScope.launch { 
            when(userRepository.deleteUser(userRepository.currentUser.value!!)){
                is Result.Success -> _dataDeleted.value = Event(Unit)
                is Result.Error -> showSnackBarMessage(R.string.error_delete_account)
                is Result.Canceled -> showSnackBarMessage(R.string.canceled)
            }
            _dataLoading.value = false
        }
        
    }

    /**
     * update the unit system preferences
     *
     * @param radioButton preferences picked
     *
     * @see updateUserPreferences
     */
    fun updateUnitSytem(radioButton: View?){
        if (radioButton is RadioButton) {
            preferencesLD.value?.unitSystem = radioButton.onClickUnitSystem()
            updateUserPreferences()
        }


    }

    /**
     * update the time display preferences
     *
     * @param radioButton preferences picked
     *
     * @see updateUserPreferences
     */
    fun updateTimeDisplay(radioButton: View?) {
        if (radioButton is RadioButton) {
            preferencesLD.value?.timeDisplay = radioButton.onClickTimeDisplay()
            updateUserPreferences()
        }

    }

    /**
     * Emit error message if the deletetion of the user 's data didn't happened
     *
     */
    fun errorDeletion(){
        showSnackBarMessage(R.string.error_deletion)
    }

    /**
     * Fet the user's preferences from the repository
     *
     * @see UserRepository.userPreferences
     *
     */
    private fun fetchPreferences(){
        preferencesLD.value = userRepository.userPreferences.value
        _dataLoading.value = false

    }

}