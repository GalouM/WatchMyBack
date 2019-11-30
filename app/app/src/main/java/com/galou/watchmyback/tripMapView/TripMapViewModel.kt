package com.galou.watchmyback.tripMapView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.Event
import com.galou.watchmyback.base.BaseViewModel

/**
 * @author galou
 * 2019-11-29
 */
class TripMapViewModel : BaseViewModel(){

    private val _openAddTripActivity = MutableLiveData<Event<Unit>>()
    val openAddTripActivity: LiveData<Event<Unit>> = _openAddTripActivity

    fun clickStartNewTrip(){
        _openAddTripActivity.value = Event(Unit)
    }
}