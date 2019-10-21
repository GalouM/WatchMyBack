package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.galou.watchmyback.utils.*

/**
 * Represent a specific Location with its longitude and latitude
 *
 * Used to specify the Location of a [StagePoint] or a [SchedulePoint] on the map.
 *
 * @property pointId id of the [TripPoint]
 * @property latitude latitude of the location
 * @property longitude longitude of the location
 * @property city name of the city where the [TripPoint] is located
 * @property country name of the country where the [TripPoint] is located
 *
 * @see StagePoint
 * @see SchedulePoint
 * @see WeatherData
 *
 * @author Galou Minisini
 */
@Entity(
    tableName = LOCATION_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = TripPoint::class,
            parentColumns = [POINT_TRIP_UUID],
            childColumns = [LOCATION_TABLE_UUID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Location(
    @ColumnInfo(name = LOCATION_TABLE_UUID) @PrimaryKey var pointId: String = "",
    @ColumnInfo(name = LOCATION_TABLE_LATITUDE) var latitude: Double = 0.0,
    @ColumnInfo(name = LOCATION_TABLE_LONGITUDE) var longitude: Double = 0.0,
    @ColumnInfo(name = LOCATION_TABLE_CITY) var city: String = "",
    @ColumnInfo(name = LOCATION_TABLE_COUNTRY) var country: String = ""

)