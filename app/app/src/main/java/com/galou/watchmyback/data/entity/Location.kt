package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.galou.watchmyback.utils.*
import java.math.RoundingMode

/**
 * Represent a specific Location with its longitude and latitude
 *
 * Used to specify the Location of a [PointTrip] on the map.
 *
 * @property pointId id of the [PointTrip]
 * @property latitude latitude of the location
 * @property longitude longitude of the location
 * @property city name of the city where the [PointTrip] is located
 * @property country name of the country where the [PointTrip] is located
 *
 * @see PointTrip
 * @see WeatherData
 * @see Entity
 *
 * @author Galou Minisini
 */
@Entity(
    tableName = LOCATION_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = PointTrip::class,
            parentColumns = [POINT_TRIP_UUID],
            childColumns = [LOCATION_TABLE_UUID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Location(
    @ColumnInfo(name = LOCATION_TABLE_UUID) @PrimaryKey var pointId: String = "",
    @ColumnInfo(name = LOCATION_TABLE_LATITUDE) var latitude: Double? = null,
    @ColumnInfo(name = LOCATION_TABLE_LONGITUDE) var longitude: Double? = null,
    @ColumnInfo(name = LOCATION_TABLE_CITY) var city: String? = null,
    @ColumnInfo(name = LOCATION_TABLE_COUNTRY) var country: String? = null

){

    var newLatitude = latitude
        set(value){
            latitude = value?.toBigDecimal()?.setScale(4, RoundingMode.HALF_UP)?.toDouble()
            field = value
        }
    var newLongitude = longitude
        set(value){
            longitude = value?.toBigDecimal()?.setScale(4, RoundingMode.HALF_UP)?.toDouble()
            field = value
        }
}