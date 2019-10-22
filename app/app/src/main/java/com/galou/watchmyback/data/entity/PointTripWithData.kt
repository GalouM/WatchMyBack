package com.galou.watchmyback.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.galou.watchmyback.utils.LOCATION_TABLE_UUID
import com.galou.watchmyback.utils.POINT_TRIP_UUID
import com.galou.watchmyback.utils.WEATHER_DATA_TABLE_POINT_UUID

/**
 * Created by galou on 2019-10-22
 */
data class PointTripWithData(
    @Embedded val pointTrip: PointTrip,
    @Relation(parentColumn = POINT_TRIP_UUID, entityColumn = LOCATION_TABLE_UUID, entity = Location::class)
    val location: List<Location>,
    @Relation(parentColumn = POINT_TRIP_UUID, entityColumn = WEATHER_DATA_TABLE_POINT_UUID, entity = WeatherData::class)
    val weatherData: List<WeatherData>
)