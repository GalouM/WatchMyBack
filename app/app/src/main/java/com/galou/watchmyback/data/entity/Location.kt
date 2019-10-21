package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.galou.watchmyback.utils.*

/**
 * Created by galou on 2019-10-20
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