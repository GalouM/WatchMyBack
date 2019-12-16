package com.galou.watchmyback.detailsPoint

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.data.repository.UserRepository

/**
 * @author galou
 * 2019-12-15
 */
class DetailsPointViewModel(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _pointTripLD = MutableLiveData<PointTripWithData>()
    val pointTripLD: LiveData<PointTripWithData> = _pointTripLD

    private val _userPreferencesLD = MutableLiveData<UserPreferences>()
    val userPreferencesLD: LiveData<UserPreferences> = _userPreferencesLD

}