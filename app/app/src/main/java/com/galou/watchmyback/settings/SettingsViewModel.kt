package com.galou.watchmyback.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TimeDisplay
import com.galou.watchmyback.data.entity.UnitSystem
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by galou on 2019-10-30
 */
class SettingsViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _dataDeleted = MutableLiveData<Event<Unit>>()
    val dataDeleted: LiveData<Event<Unit>> = _dataDeleted

    val emergencyNumberLD = MutableLiveData<String>()
    val unitSystemLD = MutableLiveData<UnitSystem>()
    val timeDisplayLD = MutableLiveData<TimeDisplay>()
    val emergencyNotificationLD = MutableLiveData<Boolean>()
    val backSafeNotificationLD = MutableLiveData<Boolean>()
    val lateNotificationLD = MutableLiveData<Boolean>()
    val updateLocationNotificationLD = MutableLiveData<Boolean>()

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarText

    private lateinit var preferences: UserPreferences
    
    private var fetchPreferencesJob: Job? = null

    init {
        val user = userRepository.currentUser.value!!
        fetchPreferences(user.id)
    }

    private fun fetchPreferences(userId: String){
        if(fetchPreferencesJob?.isActive == true) fetchPreferencesJob?.cancel()
        fetchPreferencesJob = viewModelScope.launch {
            when(val preferencesResult = userRepository.fetchUserPreferences(userId)){
                is Result.Success -> {
                    val data = preferencesResult.data
                    if (data != null){
                        preferences = data
                    } else {
                        showSnackBarMessage(R.string.fetch_preferences_error)
                    }
                }
                is Result.Error -> showSnackBarMessage(R.string.fetch_preferences_error)
                is Result.Canceled -> showSnackBarMessage(R.string.fetch_preferences_error)
            }
        }

    }

    private fun showSnackBarMessage(message: Int){
        _snackbarText.value = Event(message)
    }

}