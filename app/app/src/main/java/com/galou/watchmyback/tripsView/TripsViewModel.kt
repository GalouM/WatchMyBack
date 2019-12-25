package com.galou.watchmyback.tripsView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.applicationUse.TripDisplay
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.extension.updateStatus
import kotlinx.coroutines.launch

/**
 * @author galou
 * 2019-12-20
 */

class TripsViewModel(
    private val userRepository: UserRepository,
    private val tripRepository: TripRepository
) : BaseViewModel(){

    private val _tripsLD = MutableLiveData<List<TripDisplay>>()
    val tripsLD: LiveData<List<TripDisplay>> = _tripsLD

    private val _tripSelectedLD = MutableLiveData<Event<String>>()
    val tripSelectedLD: LiveData<Event<String>> = _tripSelectedLD

    val userLD: LiveData<User> = userRepository.currentUser

    fun onClickTrip(trip: TripDisplay){
        _tripSelectedLD.value = Event(trip.tripId)

    }

    fun refreshTripList(){
        fetchTripsWatching()

    }

    fun fetchTripsWatching(){
        _dataLoading.value = true
        viewModelScope.launch { 
            when(val result = tripRepository.fetchTripUserWatching(userLD.value?.id ?: throw Exception("no user connected ${userLD.value}"))){
                is Result.Success -> {
                    fetchTripOwnerInfo(result.data.apply { updateStatus() })
                }
                else -> {
                    showSnackBarMessage(R.string.error_fetch_trips_watching)
                    _dataLoading.value = false
                }
            }
            
        }

    }
    
    private fun fetchTripOwnerInfo(trips: List<TripWithData>){
        viewModelScope.launch {
            val userPrefs = userRepository.userPreferences.value?: throw Exception("No user pref set")
            when(val result = tripRepository.convertTripForDisplay(trips, userPrefs)){
                is Result.Success ->  {
                    _tripsLD.value = result.data
                }
                else -> showSnackBarMessage(R.string.error_fetch_owner)
            }
            _dataLoading.value = false
        }
        
    }

}