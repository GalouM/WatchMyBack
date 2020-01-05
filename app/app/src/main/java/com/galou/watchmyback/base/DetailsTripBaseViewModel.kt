package com.galou.watchmyback.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.data.entity.Trip
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
abstract class DetailsTripBaseViewModel(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    protected var currentTrip: TripWithData? = null

    private val _centerCameraUserLD = MutableLiveData<Event<Unit>>()
    val centerCameraUserLD: LiveData<Event<Unit>> = _centerCameraUserLD

    protected val _pointsCoordinateLD = MutableLiveData<List<Map<String, Coordinate>>>()
    val pointsCoordinateLD: LiveData<List<Map<String, Coordinate>>> = _pointsCoordinateLD

    private val _showPointDetailsLD = MutableLiveData<Event<Unit>>()
    val showPointDetailsLD: LiveData<Event<Unit>> = _showPointDetailsLD

    val userLD: LiveData<User> = userRepository.currentUser

    protected val _tripLD = MutableLiveData<Trip>()
    val tripLD: LiveData<Trip> = _tripLD

    abstract fun emitTripInfo(trip: TripWithData)

    /**
     * Center the map camera on the user
     *
     */
    fun centerCameraOnUser() {
        _centerCameraUserLD.value = Event(Unit)
    }

    /**
     * Show message that GPS is off
     *
     */
    fun gpsNotAvailable(){
        showSnackBarMessage(R.string.turn_on_gps)
    }

    /**
     * Received the point id of the point selected by the user
     * Show the details of this point
     *
     * @param pointId ID of the point
     */
    fun clickPointTrip(pointId: String){
        val selectedPoint = currentTrip?.points?.first{ it.pointTrip.id == pointId }
            ?: throw Exception("Error finding the point with id $pointId for the trip $currentTrip")
        tripRepository.pointSelected = selectedPoint
        _showPointDetailsLD.value = Event(Unit)

    }

    /**
     * Fetch the active trip of the current user
     *
     * @see TripRepository.fetchUserActiveTrip
     *
     */
    protected fun fetchActiveTrip(){
        viewModelScope.launch {
            val userId = userLD.value?.id ?: throw Exception("No current user set")
            when(val trip = tripRepository.fetchUserActiveTrip(userId, false)){
                is Result.Success -> {
                    if (trip.data != null) {
                        emitTripInfo(trip.data.apply { updateStatus() })
                    }
                    else {
                        _tripLD.value = null
                        _pointsCoordinateLD.value = null
                        currentTrip = null
                        showSnackBarMessage(R.string.no_current_active_trip)
                        _dataLoading.value = false
                    }
                }
                else -> {
                    showSnackBarMessage(R.string.error_fetch_trip)
                    _dataLoading.value = false
                }
            }
        }

    }

}