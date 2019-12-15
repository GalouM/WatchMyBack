package com.galou.watchmyback.mapPickLocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.data.repository.TripRepository

/**
 * ViewModel for [PickLocationActivity]
 *
 * Inherit from [BaseViewModel]
 *
 * @property tripRepository [TripRepository] reference
 */
class PickLocationMapViewModel(
    private val tripRepository: TripRepository
) : BaseViewModel() {

    private var newCoordinate: Coordinate? = null

    private val _pointsTripLocationLD = MutableLiveData<List<Coordinate>>()
    val pointsTripLocationLD: LiveData<List<Coordinate>> = _pointsTripLocationLD

    private val _pointSelectedLocation = MutableLiveData<Event<Coordinate>>()
    val pointSelectedLocation: LiveData<Event<Coordinate>> = _pointSelectedLocation

    private val _centerCameraLD = MutableLiveData<Event<Unit>>()
    val centerCameraLD: LiveData<Event<Unit>> = _centerCameraLD

    private val _validateNewCoordinateLD = MutableLiveData<Event<Coordinate>>()
    val validateNewCoordinateLD: LiveData<Event<Coordinate>> = _validateNewCoordinateLD

    init {
        showSnackBarMessage(R.string.click_on_map)
    }

    /**
     * Display the trip points location on the map
     *
     * @see showPointSelected
     * @see showTripPoint
     *
     */
    fun onMapReady(){
        _dataLoading.value = true
        showPointSelected()
        showTripPoint()
        _dataLoading.value = false

    }

    /**
     * Update the point position on the map and save the new coordinate
     *
     * @param latitude new latitude of the point
     * @param longitude new longitude of the point
     */
    fun updatePointPosition(latitude: Double, longitude: Double){
        newCoordinate = Coordinate(latitude, longitude)
        showPointSelected()
    }

    /**
     * Center the map camera on the user
     *
     */
    fun centerCameraOnUser(){
        _centerCameraLD.value = Event(Unit)
    }

    /**
     * Validate the new coordinate entered or show a helper message 
     * if no coordinates has been entered
     *
     */
    fun validateCoordinate(){
        if (newCoordinate != null){
            _validateNewCoordinateLD.value = Event(newCoordinate!!)
        } else {
            showSnackBarMessage(R.string.click_on_map)
        }

    }

    /**
     * Show the Location of the selected point on the map
     *
     */
    private fun showPointSelected(){
        if (newCoordinate == null){
            with(tripRepository.pointSelected?.location){
                this?.latitude?.let { latitude ->
                    longitude?.let { longitude ->
                        _pointSelectedLocation.value = Event(Coordinate(latitude, longitude))

                    }
                }
            }
        } else {
            _pointSelectedLocation.value = Event(newCoordinate!!)
        }

    }

    /**
     * Show the location of all the trip points on the map
     *
     */
    private fun showTripPoint(){
        with(tripRepository.tripPoints?.filter { it != tripRepository.pointSelected }?.mapNotNull { it.location }){
            val listLocation = mutableListOf<Coordinate>()
            this?.forEach { location ->
                location.latitude?.let { latitude ->
                    location.longitude?.let { longitude ->
                        listLocation.add(Coordinate(latitude, longitude))
                    }
                }
            }

            _pointsTripLocationLD.value = listLocation

        }
    }
}