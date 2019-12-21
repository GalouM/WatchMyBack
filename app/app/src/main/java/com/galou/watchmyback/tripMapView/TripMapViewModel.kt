package com.galou.watchmyback.tripMapView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.Event
import com.galou.watchmyback.base.DetailsTripBaseViewModel
import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.entity.TypePoint
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.data.repository.UserRepository

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
    fun clickStartNewTrip(){
        _openAddTripActivity.value = Event(Unit)
    }

    /**
     * Fetch the the user active trip and display the points
     * @see TripRepository.fetchUserActiveTrip
     * @see emitPointTripLocation
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
        trip.points.forEach { point ->
            val coordinate = Coordinate(
                latitude = point.location?.latitude ?: throw Exception("No latitude for this point $point") ,
                longitude = point.location.longitude ?: throw Exception("No longitude for this point $point"))
            when(point.pointTrip.typePoint){
                TypePoint.START, TypePoint.END, TypePoint.SCHEDULE_STAGE ->
                    schedulePoints[point.pointTrip.id] = coordinate
                TypePoint.CHECKED_UP -> checkedPoints[point.pointTrip.id] = coordinate
            }
        }
        _schedulePointsLD.value = schedulePoints
        _checkedPointsLD.value = checkedPoints
        _dataLoading.value = false

    }
}