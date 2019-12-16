package com.galou.watchmyback.tripMapView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.entity.TypePoint
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.Result
import kotlinx.coroutines.launch

/**
 * @author galou
 * 2019-11-29
 */
class TripMapViewModel(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository
) : BaseViewModel(){

    private var currentTrip: TripWithData? = null

    private val _openAddTripActivity = MutableLiveData<Event<Unit>>()
    val openAddTripActivity: LiveData<Event<Unit>> = _openAddTripActivity

    private val _centerCameraUserLD = MutableLiveData<Event<Unit>>()
    val centerCameraUserLD: LiveData<Event<Unit>> = _centerCameraUserLD

    private val _startPointLD = MutableLiveData<Map<String, Coordinate>>()
    val startPointLD: LiveData<Map<String, Coordinate>> = _startPointLD

    private val _endPointLD = MutableLiveData<Map<String, Coordinate>>()
    val endPointLD: LiveData<Map<String, Coordinate>> = _endPointLD

    private val _schedulePointsLD = MutableLiveData<Map<String, Coordinate>>()
    val schedulePointsLD: LiveData<Map<String, Coordinate>> = _schedulePointsLD

    private val _checkedPointsLD = MutableLiveData<Map<String, Coordinate>>()
    val checkedPointsLD: LiveData<Map<String, Coordinate>> = _checkedPointsLD

    private val _showPointDetailsLD = MutableLiveData<Event<Unit>>()
    val showPointDetailsLD: LiveData<Event<Unit>> = _showPointDetailsLD

    val userLD: LiveData<User> = userRepository.currentUser

    /**
     * Show activity to start a new trip
     *
     */
    fun clickStartNewTrip(){
        _openAddTripActivity.value = Event(Unit)
    }

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
     * Fetch the the user active trip and display the points
     * @see TripRepository.fetchUserActiveTrip
     * @see emitPointTripLocation
     *
     */
    fun fetchAndDisplayUserActiveTrip(){
        _dataLoading.value = true
        viewModelScope.launch { 
            when(val activeTrip = tripRepository.fetchUserActiveTrip(userLD.value!!.id)){
                is Result.Success -> activeTrip.data?.let {
                    currentTrip = it
                    emitPointTripLocation()
                }
                else -> {
                    showSnackBarMessage(R.string.error_fetching_current_trip)
                    _dataLoading.value = false
                }
            }
        }

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
     * Order the points by type and create map to emit ID and coordinate of the points
     *
     */
    private fun emitPointTripLocation(){
        currentTrip?.points?.let { points ->
            val schedulePoints = mutableMapOf<String, Coordinate>()
            val checkedPoints = mutableMapOf<String, Coordinate>()
            points.forEach { point ->
                val coordinate = Coordinate(
                    latitude = point.location?.latitude ?: throw Exception("No latitude for this point $point") ,
                    longitude = point.location.longitude ?: throw Exception("No longitude for this point $point"))
                when(point.pointTrip.typePoint){
                    TypePoint.START -> _startPointLD.value = mapOf(point.pointTrip.id to coordinate)
                    TypePoint.END -> _endPointLD.value = mapOf(point.pointTrip.id to coordinate)
                    TypePoint.SCHEDULE_STAGE -> schedulePoints[point.pointTrip.id] = coordinate
                    TypePoint.CHECKED_UP -> checkedPoints[point.pointTrip.id] = coordinate
                }
            }
            _schedulePointsLD.value = schedulePoints
            _checkedPointsLD.value = checkedPoints

        }
        _dataLoading.value = false

    }
}