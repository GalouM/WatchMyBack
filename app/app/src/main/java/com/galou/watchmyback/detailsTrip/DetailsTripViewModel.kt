package com.galou.watchmyback.detailsTrip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.Result
import kotlinx.coroutines.launch

/**
 * @author galou
 * 2019-12-18
 */

class DetailsTripViewModel(
    val tripRepository: TripRepository,
    val userRepository: UserRepository
) : BaseViewModel(){

    private val _tripLD = MutableLiveData<TripWithData>()
    val tripLD: LiveData<TripWithData> = _tripLD

    val userLD :LiveData<User> = userRepository.currentUser

    val userPrefsLD: LiveData<UserPreferences> = userRepository.userPreferences

    private val _startPointLD = MutableLiveData<PointTripWithData>()
    val startPointLD: LiveData<PointTripWithData> = _startPointLD

    private val _endPointLD = MutableLiveData<PointTripWithData>()
    val endPointLD: LiveData<PointTripWithData> = _endPointLD

    private val _lastPointCheckedLD = MutableLiveData<PointTripWithData>()
    val lastPointCheckedLD: LiveData<PointTripWithData> = _lastPointCheckedLD

    private val _tripIsDoneLD = MutableLiveData<Boolean>()
    val tripIsDoneLD: LiveData<Boolean> = _tripIsDoneLD

    private val _emergencyNumberLD = MutableLiveData<Event<String>>()
    val emergencyNumberLD: LiveData<Event<String>> = _emergencyNumberLD

    private val _tripOwnerNumberLD = MutableLiveData<Event<String>>()
    val tripOwnerNumberLD: LiveData<Event<String>> = _tripOwnerNumberLD

    private val _tripOwnerNameLD = MutableLiveData<String>()
    val tripOwnerNameLD: LiveData<String> = _tripOwnerNameLD

    private var tripOwner: User? = null

    fun fetchTripInfo(tripId: String? = null){
        _dataLoading.value = true
        if (tripId != null) fetchTripById(tripId)
        else fetchActiveTrip()

    }

    fun callEmergency(){
        with(userPrefsLD.value ?: throw Exception("No user prefs setup")){
            if (emergencyNumber.isBlank()){
                println("blank")
                showSnackBarMessage(R.string.no_emergency_number)
            } else {
                println(userPrefsLD.value)
                _emergencyNumberLD.value = Event(emergencyNumber)
            }
        }

    }

    fun callTripOwner(){
        if (tripOwner?.phoneNumber != null){
            _tripOwnerNumberLD.value = Event(tripOwner!!.phoneNumber!!)
        } else {
            showSnackBarMessage(R.string.no_phone_number)
        }
        

    }

    private fun fetchTripById(tripId: String){
        viewModelScope.launch {
            val trip = tripRepository.fetchTrip(tripId)
            if (trip is Result.Success && trip.data != null){
                _tripLD.value = trip.data
                findMainsInfo(trip.data)
                fetchUserInfo(trip.data.trip.userId)
            } else {
                showSnackBarMessage(R.string.error_fetch_trip)
            }
        }
    }

    private fun fetchActiveTrip(){
        viewModelScope.launch { 
            val userId = userLD.value?.id ?: throw Exception("No current user set")
            when(val trip = tripRepository.fetchUserActiveTrip(userId)){
                is Result.Success -> {
                    if (trip.data != null) {
                        _tripLD.value = trip.data
                        findMainsInfo(trip.data)
                    }
                    else showSnackBarMessage(R.string.no_current_active_trip)
                }
                else -> showSnackBarMessage(R.string.error_fetch_trip)
            }
        }

    }

    private fun fetchUserInfo(userId: String){
        viewModelScope.launch {
            when(val result = userRepository.fetchTripOwner(userId)){
                is Result.Success -> {
                    tripOwner = result.data
                    _tripOwnerNameLD.value = tripOwner!!.username
                }
            }
        }
    }

    private fun findMainsInfo(trip: TripWithData){
        _tripIsDoneLD.value = trip.trip.status == TripStatus.BACK_SAFE
        with(trip.points){
            _startPointLD.value = find { it.pointTrip.typePoint == TypePoint.START }
            _endPointLD.value = find { it.pointTrip.typePoint == TypePoint.END }
            _lastPointCheckedLD.value = when (trip.trip.status){
                TripStatus.BACK_SAFE -> endPointLD.value
                else -> findLast { it.pointTrip.typePoint == TypePoint.CHECKED_UP }
            }
            if (lastPointCheckedLD.value == null ){
                showSnackBarMessage(R.string.no_checkup_yet)
            }
        }

    }

}