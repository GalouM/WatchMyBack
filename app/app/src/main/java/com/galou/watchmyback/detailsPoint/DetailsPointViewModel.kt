package com.galou.watchmyback.detailsPoint

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.TypePoint
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.data.repository.UserRepository

/**
 * @author galou
 * 2019-12-15
 */
class DetailsPointViewModel(
    tripRepository: TripRepository,
    userRepository: UserRepository
) : BaseViewModel() {

    private val _pointTripLD = MutableLiveData<PointTripWithData>()
    val pointTripLD: LiveData<PointTripWithData> = _pointTripLD

    val userPreferencesLD: LiveData<UserPreferences> = userRepository.userPreferences

    private val _isScheduledPoint = MutableLiveData<Boolean>()
    val isScheduledPoint: LiveData<Boolean> = _isScheduledPoint

    init {
        _pointTripLD.value = tripRepository.pointSelected
        _isScheduledPoint.value = when(tripRepository.pointSelected?.pointTrip?.typePoint){
            TypePoint.CHECKED_UP -> false
            else -> true
        }
    }

}