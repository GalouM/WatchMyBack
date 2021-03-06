package com.galou.watchmyback.detailsTrip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.base.DetailsTripBaseViewModel
import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.data.repository.CheckListRepository
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.extension.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * @author galou
 * 2019-12-18
 */

class DetailsTripViewModel(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository,
    private val checkListRepository: CheckListRepository
) : DetailsTripBaseViewModel(tripRepository, userRepository){

    val userPrefsLD: LiveData<UserPreferences> = userRepository.userPreferences

    private val _startPointTimeLD = MutableLiveData<Date>()
    val startPointTimeLD: LiveData<Date> = _startPointTimeLD

    private val _endPointTimeLD = MutableLiveData<Date>()
    val endPointTimeLD: LiveData<Date> = _endPointTimeLD

    private val _startPointWeatherLD = MutableLiveData<WeatherData>()
    val startPointWeatherLD: LiveData<WeatherData> = _startPointWeatherLD

    private val _endPointWeatherLD = MutableLiveData<WeatherData>()
    val endPointWeatherLD: LiveData<WeatherData> = _endPointWeatherLD

    private val _lastPointCheckedLD = MutableLiveData<PointTripWithData>()
    val lastPointCheckedLD: LiveData<PointTripWithData> = _lastPointCheckedLD

    private val _lastPointCoordinate = MutableLiveData<Map<String, Coordinate>>()
    val lastPointCoordinate: LiveData<Map<String, Coordinate>> = _lastPointCoordinate

    private val _tripIsDoneLD = MutableLiveData<Boolean>()
    val tripIsDoneLD: LiveData<Boolean> = _tripIsDoneLD

    private val _emergencyNumberLD = MutableLiveData<Event<String>>()
    val emergencyNumberLD: LiveData<Event<String>> = _emergencyNumberLD

    private val _tripOwnerNumberLD = MutableLiveData<Event<String>>()
    val tripOwnerNumberLD: LiveData<Event<String>> = _tripOwnerNumberLD

    private val _tripOwnerNameLD = MutableLiveData<String>()
    val tripOwnerNameLD: LiveData<String> = _tripOwnerNameLD

    private val _tripWatchersLD = MutableLiveData<List<User>>()
    val tripWatchersLD: LiveData<List<User>> = _tripWatchersLD

    private val _itemsCheckListLD = MutableLiveData<List<ItemCheckList>>()
    val itemsCheckListLD: LiveData<List<ItemCheckList>> = _itemsCheckListLD

    private var tripOwner: User? = null


    /**
     * Fetch the trip with the corresponding ID or if null the user's active trip
     *
     * @param tripId trip Id to fetch
     *
     * @see fetchTripById
     * @see fetchActiveTrip
     */
    fun fetchTripInfo(tripId: String? = null){
        _dataLoading.value = true
        if (tripId != null) fetchTripById(tripId)
        else fetchActiveTrip()

    }

    /**
     * Emit the emergency number saved by the user
     *
     */
    fun callEmergency(){
        with(userPrefsLD.value ?: throw Exception("No user prefs setup")){
            if (emergencyNumber.isBlank()){
                showSnackBarMessage(R.string.no_emergency_number)
            } else {
                println(userPrefsLD.value)
                _emergencyNumberLD.value = Event(emergencyNumber)
            }
        }

    }

    /**
     * Emit the trip's owner phone number
     *
     */
    fun callTripOwner(){
        if (tripOwner?.phoneNumber != null){
            _tripOwnerNumberLD.value = Event(tripOwner!!.phoneNumber!!)
        } else {
            showSnackBarMessage(R.string.no_phone_number)
        }
        

    }

    fun clickLatestPointLocation(){
        lastPointCheckedLD.value?.pointTrip?.id?.let {
            clickPointTrip(it)
        }
    }

    fun reEmitPointLocation(){
        _pointsCoordinateLD.emitNewValue()
        _lastPointCoordinate.emitNewValue()
    }

    /**
     * Fetch a trip information from its it
     *
     * @param tripId ID of the trip to fetch
     *
     * @see TripRepository.fetchTrip
     */
    private fun fetchTripById(tripId: String){
        viewModelScope.launch {
            val trip = tripRepository.fetchTrip(tripId)
            if (trip is Result.Success && trip.data != null){
                emitTripInfo(trip.data.apply { updateStatus() })
                fetchUserInfo(trip.data.trip.userId)
            } else {
                showSnackBarMessage(R.string.error_fetch_trip)
                _dataLoading.value = false
            }
        }
    }

    /**
     * Fetch the trip's owner information
     *
     * @param userId Id of the trip's owner
     *
     * @see UserRepository.fetchTripOwner
     */
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

    /**
     * Fetch the trip's checklist
     *
     * @param checkListId Id of the checklist
     *
     * @see CheckListRepository.fetchCheckList
     */
    private fun fetchCheckListItems(checkListId: String){
        viewModelScope.launch {
            when(val result = checkListRepository.fetchCheckList(checkListId, true)){
                is Result.Success -> _itemsCheckListLD.value = result.data?.items
                else -> showSnackBarMessage(R.string.error_fetch_trip_checklist)
            }
        }
    }

    override fun emitTripInfo(trip: TripWithData){
        currentTrip = trip
        trip.trip.checkListId?.let { fetchCheckListItems(it) }
        _tripLD.value = trip.trip
        _tripWatchersLD.value = trip.watchers
        _tripIsDoneLD.value = trip.trip.status == TripStatus.BACK_SAFE
        emitPointTripLocation(trip.points)
    }

    /**
     * Order the points by type and create a Map to emit ID and coordinate of the points
     *
     */
    private fun emitPointTripLocation(points: List<PointTripWithData>){
        val schedulePoints = mutableMapOf<String, Coordinate>()
        val checkedPoints = mutableMapOf<String, Coordinate>()
        val startEndPoints = mutableMapOf<String, Coordinate>()
         points.forEach { point ->
             when(point.pointTrip.typePoint){
                 TypePoint.START -> {
                     startEndPoints[point.pointTrip.id] = point.getCoordinate()
                     _startPointTimeLD.value = point.pointTrip.time
                     _startPointWeatherLD.value = point.weatherData
                     if(currentTrip?.trip?.status != TripStatus.BACK_SAFE){
                         _lastPointCoordinate.value = point.createMapCoordinate()
                     }
                 }
                 TypePoint.END -> {
                     startEndPoints[point.pointTrip.id] = point.getCoordinate()
                     _endPointTimeLD.value = point.pointTrip.time
                     _endPointWeatherLD.value = point.weatherData
                     if (currentTrip?.trip?.status == TripStatus.BACK_SAFE) {
                         _lastPointCheckedLD.value = point
                         _lastPointCoordinate.value = point.createMapCoordinate()
                     }
                 }
                 TypePoint.SCHEDULE_STAGE -> schedulePoints[point.pointTrip.id] = point.getCoordinate()
                 TypePoint.CHECKED_UP -> checkedPoints[point.pointTrip.id] = point.getCoordinate()

             }
         }
        if(checkedPoints.isNotEmpty()){
            val latestPoint = points.findLatestCheckUpPoint()
            if(currentTrip?.trip?.status != TripStatus.BACK_SAFE) _lastPointCheckedLD.value = latestPoint
            _lastPointCoordinate.value = latestPoint?.createMapCoordinate()
        }

        _pointsCoordinateLD.value = listOf(startEndPoints, schedulePoints, checkedPoints)

        _dataLoading.value = false

    }

}