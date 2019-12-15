package com.galou.watchmyback.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.galou.watchmyback.utils.LOCATION_TABLE_UUID
import com.galou.watchmyback.utils.POINT_TRIP_UUID
import com.galou.watchmyback.utils.WEATHER_DATA_TABLE_POINT_UUID

/**
 * Represent a [PointTrip] and its data
 *
 * [Embedded] object with its [Relation] so Room can return a [PointTrip] with all the data connected to it
 *
 * @property pointTrip [PointTrip] queried
 * @property location [Location] of the point
 * @property weatherData [WeatherData] of the point
 *
 * @see PointTrip
 * @see Location
 * @see WeatherData
 * @see Embedded
 * @see Relation
 *
 * @author Galou Minisini
 */
data class PointTripWithData(
    @Embedded val pointTrip: PointTrip = PointTrip(),
    @Relation(
        parentColumn = POINT_TRIP_UUID,
        entityColumn = LOCATION_TABLE_UUID,
        entity = Location::class
    )
    val location: Location? = Location(pointId = pointTrip.id),
    @Relation(
        parentColumn = POINT_TRIP_UUID,
        entityColumn = WEATHER_DATA_TABLE_POINT_UUID,
        entity = WeatherData::class
    )
    val weatherData: WeatherData? = WeatherData(pointId = pointTrip.id)
)