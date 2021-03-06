package com.galou.watchmyback.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.repository.CheckListRepository
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.data.repository.UserRepositoryImpl
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.extension.onClickTimeDisplay
import com.galou.watchmyback.utils.extension.onClickUnitSystem
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
open class SettingsViewModel(
    private val userRepository: UserRepository,
    private val checkListRepository: CheckListRepository,
    private val tripRepository: TripRepository
) : BaseViewModel() {

    private val _dataDeleted = MutableLiveData<Event<Unit>>()
    val dataDeleted: LiveData<Event<Unit>> = _dataDeleted

    private val _dataSaved = MutableLiveData<Event<Unit>>()
    val dataSaved: LiveData<Event<Unit>> = _dataSaved

    val preferencesLD = MutableLiveData<UserPreferences>()

    private val _enableLateNotificationLD = MutableLiveData<Event<String>>()
    val enableLateNotificationLD: LiveData<Event<String>> = _enableLateNotificationLD

    private val _enableBackHomeNotificationLD = MutableLiveData<Event<String>>()
    val enableBackHomeNotificationLD: LiveData<Event<String>> = _enableBackHomeNotificationLD

    private val _disableLateNotificationLD = MutableLiveData<Event<Unit>>()
    val disableLateNotificationLD: LiveData<Event<Unit>> = _disableLateNotificationLD

    private val _disableBackHomeNotificationLD = MutableLiveData<Event<Unit>>()
    val disableBackHomeNotificationLD: LiveData<Event<Unit>> = _disableBackHomeNotificationLD

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
        viewModelScope.launch {
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

        fun deleteUser(user: User){
            viewModelScope.launch {
                when(val task = userRepository.deleteUser(user)){
                    is Result.Success -> _dataDeleted.value = Event(Unit)
                    else -> showSnackBarMessage(R.string.error_delete_account)
                }
                _dataLoading.value = false
            }
        }

        fun deleteUserCheckList(user: User){
            viewModelScope.launch {
                viewModelScope.launch {
                    when(val task = checkListRepository.deleteUserCheckList(user.id)){
                        is Result.Success -> deleteUser(user)
                        else -> {
                            showSnackBarMessage(R.string.error_delete_account)
                            _dataLoading.value = false
                        }
                    }
                }
            }
        }

        fun deleteUserTrip(user: User){
            viewModelScope.launch {
                when(val task = tripRepository.deleteUserTrips(user.id)){
                    is Result.Success -> deleteUserCheckList(user)
                    else -> {
                        showSnackBarMessage(R.string.error_delete_account)
                        _dataLoading.value = false
                    }
                }
            }
        }

        _dataLoading.value = true
        deleteUserTrip(userRepository.currentUser.value ?: throw Exception("No user set"))


        
    }

    /**
     * update the unit system preferences
     *
     * @param buttonId ID of the button clicked
     *
     * @see updateUserPreferences
     */
    fun updateUnitSytem(buttonId: Int){
        preferencesLD.value?.unitSystem = buttonId.onClickUnitSystem()
        updateUserPreferences()



    }

    /**
     * update the time display preferences
     *
     * @param buttonId ID of the button clicked
     *
     * @see updateUserPreferences
     */
    fun updateTimeDisplay(buttonId: Int) {
        preferencesLD.value?.timeDisplay = buttonId.onClickTimeDisplay()
        updateUserPreferences()


    }

    /**
     * Emit error message if the deletion of the user 's data didn't happened
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

    fun clickNotificationLate(enable: Boolean){
        if (enable) _enableLateNotificationLD.value = Event(userRepository.currentUser.value?.id ?: throw Exception("No current user set"))
        else _disableLateNotificationLD.value = Event(Unit)
        preferencesLD.value?.notificationLate = enable
        updateUserPreferences()
    }

    fun clickNotificationBackHome(enable: Boolean){
        if (enable) _enableBackHomeNotificationLD.value = Event(userRepository.currentUser.value?.id ?: throw Exception("No current user set"))
        else _disableBackHomeNotificationLD.value = Event(Unit)
        preferencesLD.value?.notificationBackSafe = enable
        updateUserPreferences()
    }

}