package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.galou.watchmyback.utils.*

/**
 * Created by galou on 2019-10-20
 */

@Entity(
    tableName = WEATHER_DATA_TABLE_NAME
)
data class WeatherData(
    @ColumnInfo(name = WEATHER_DATA_TABLE_UUID) @PrimaryKey var uuid: String = idGenerated,
    @ColumnInfo(name = WEATHER_DATA_CONDITION) var condition: String = "",
    @ColumnInfo(name = WEATHER_DATA_TEMPERATURE) var temperature: String = ""
)