package com.galou.watchmyback.data.applicationUse

import com.galou.watchmyback.data.entity.TripStatus
import com.galou.watchmyback.data.entity.TripType
import com.galou.watchmyback.data.entity.WeatherCondition

/**
 * @author galou
 * 2019-12-20
 */
data class TripDisplay(
    val tripType: TripType,
    val tripLocation: String,
    val tripStatus: TripStatus,
    val startTime: String,
    val endTime: String,
    val tripOwnerName: String,
    val weatherCondition: WeatherCondition,
    val temperature: String,
    val tripId: String
    )