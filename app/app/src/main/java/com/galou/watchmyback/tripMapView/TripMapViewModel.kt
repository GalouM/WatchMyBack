package com.galou.watchmyback.tripMapView

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.base.DetailsTripBaseViewModel
import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.data.entity.TripStatus
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.entity.TypePoint
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.CHECK_UP_WORKER_TAG
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.displayData
import kotlinx.coroutines.launch

/**
 * @author galou
 * 2019-11-29
 */
class TripMapViewModel(
    private val tripRepository: TripRepository,
    userRepository: UserRepository
) : DetailsTripBaseViewModel(tripRepository, userRepository){

    private val _openAddTripActivity = MutableLiveData<Event<Unit>>()
    val openAddTripActivity: LiveData<Event<Unit>> = _openAddTripActivity

    /**
     * Show activity to start a new trip
     *
     */
    fun clickStartStop(context: Context){
        if (currentTrip == null){
            startNewTrip()
        } else {
            stopCurrentTrip(context)
        }

    }

    /**
     * Fetch the the user active trip and display the points
     * @see fetchActiveTrip
     *
     */
    fun fetchAndDisplayUserActiveTrip(){
        fetchActiveTrip()

    }

    /**
     * Order the points by type and create map to emit ID and coordinate of the points
     *
     */
    override fun emitTripInfo(trip: TripWithData){
        currentTrip = trip
        _tripLD.value = trip.trip
        val schedulePoints = mutableMapOf<String, Coordinate>()
        val checkedPoints = mutableMapOf<String, Coordinate>()
        val startEndPoints = mutableMapOf<String, Coordinate>()
        trip.points.forEach { point ->
            val coordinate = Coordinate(
                latitude = point.location?.latitude ?: throw Exception("No latitude for this point $point") ,
                longitude = point.location.longitude ?: throw Exception("No longitude for this point $point"))
            when(point.pointTrip.typePoint){
                TypePoint.SCHEDULE_STAGE ->
                    schedulePoints[point.pointTrip.id] = coordinate
                TypePoint.CHECKED_UP -> checkedPoints[point.pointTrip.id] = coordinate
                TypePoint.START, TypePoint.END -> startEndPoints[point.pointTrip.id] = coordinate
            }
        }
        _schedulePointsLD.value = schedulePoints
        _checkedPointsLD.value = checkedPoints
        _startEndPointsLD.value = startEndPoints
        _dataLoading.value = false

    }

    private fun startNewTrip(){
        _openAddTripActivity.value = Event(Unit)
    }

    private fun stopCurrentTrip(context: Context){
        _dataLoading.value = true
        cancelWorkManager(context)
        viewModelScope.launch {
            currentTrip?.trip?.let { trip ->
                trip.status = TripStatus.BACK_SAFE
                trip.active = false
            }
            when(val result = tripRepository.updateTripStatus(currentTrip!!)){
                is Result.Error -> displayData("${result.exception}")
                is Result.Canceled -> showSnackBarMessage(R.string.error_update_trip)
                is Result.Success -> {
                    _tripLD.value = null
                    _schedulePointsLD.value = null
                    _startEndPointsLD.value = null
                    _checkedPointsLD.value = null
                    currentTrip = null
                    showSnackBarMessage(R.string.back_home_safe)
                }
                
            }
            _dataLoading.value = false
        }

    }

    private fun cancelWorkManager(context: Context){
        WorkManager.getInstance(context).cancelAllWorkByTag(CHECK_UP_WORKER_TAG)
    }
}