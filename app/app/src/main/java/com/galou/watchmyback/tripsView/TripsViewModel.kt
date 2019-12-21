package com.galou.watchmyback.tripsView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.Event
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.applicationUse.TripDisplay
import com.galou.watchmyback.data.entity.Trip

/**
 * @author galou
 * 2019-12-20
 */

class TripsViewModel : BaseViewModel(){

    private val _tripsLD = MutableLiveData<List<TripDisplay>>()
    val tripsLD: LiveData<List<TripDisplay>> = _tripsLD

    private val _tripSelectedLD = MutableLiveData<Event<String>>()
    val tripSelectedLD: LiveData<Event<String>> = _tripSelectedLD

    fun onClickTrip(trip: Trip){

    }

    fun refreshTripList(){

    }

}