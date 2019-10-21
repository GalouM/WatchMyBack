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
 * @property uuid unique id generated randomly use to identify an location
 * @property latitude latitude of the location
 * @property longitude longitude of the location
 * @property weatherDataId ID of the [WeatherData] attached to this location
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
        entity = WeatherData::class,
        parentColumns = [WEATHER_DATA_TABLE_UUID],
        childColumns = [LOCATION_TABLE_WEATHER_DATA],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class Location(
    @ColumnInfo(name = LOCATION_TABLE_UUID) @PrimaryKey var uuid: String = idGenerated,
    @ColumnInfo(name = LOCATION_TABLE_LATITUDE) var latitude: Double = 0.0,
    @ColumnInfo(name = LOCATION_TABLE_LONGITUDE) var longitude: Double = 0.0,
    @ColumnInfo(name = LOCATION_TABLE_WEATHER_DATA) var weatherDataId: String? = ""

)