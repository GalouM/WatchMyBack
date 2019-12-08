package com.galou.watchmyback.data.entity

import androidx.room.*
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.WeatherCondition.*
import com.galou.watchmyback.utils.*
import java.util.*

/**
 * Represent the Weather Data of a [Location] at a certain time
 *
 * @property id unique id generated randomly use to identify the data
 * @property pointId id of the point this data belongs to
 * @property condition condition of the weather (rain, sun...)
 * @property temperature temperature in Kelvin
 * @property dateTime time of this weather data
 *
 * @see Location
 * @see Date
 * @see WeatherCondition
 * @see Entity
 *
 * @author Galou Minisini
 */

@Entity(
    tableName = WEATHER_DATA_TABLE_NAME,
    indices = [Index(value = [WEATHER_DATA_TABLE_POINT_UUID], unique = false)],
    foreignKeys = [
        ForeignKey(
            entity = PointTrip::class,
            parentColumns = [POINT_TRIP_UUID],
            childColumns = [WEATHER_DATA_TABLE_POINT_UUID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WeatherData(
    @ColumnInfo(name = WEATHER_DATA_TABLE_UUID) @PrimaryKey var id: String = idGenerated,
    @ColumnInfo(name = WEATHER_DATA_TABLE_POINT_UUID) var pointId: String = "",
    @ColumnInfo(name = WEATHER_DATA_TABLE_CONDITION) var condition: WeatherCondition = CLEAR_SKY,
    @ColumnInfo(name = WEATHER_DATA_TABLE_TEMPERATURE) var temperature: Double = 0.0,
    @ColumnInfo(name = WEATHER_DATA_TABLE_DATETIME) var dateTime: Date? = null,
    @ColumnInfo(name = WEATHER_DATA_TABLE_ICON) var iconName: String = ""
)

/**
 * Represent the different type of weather condition possible
 *
 * @property CLEAR_SKY
 * @property FEW_CLOUDS
 * @property SCATTERED_CLOUDS
 * @property BROKEN_CLOUDS
 * @property SHOWER_RAIN
 * @property THUNDERSTORM
 * @property SNOW
 * @property MIST
 *
 * @param conditionNameId ID of the String that represent the name of the condition
 *
 * @see WeatherData
 *
 * @author Galou Minisini
 */
enum class WeatherCondition(val conditionNameId: Int){
    CLEAR_SKY(R.string.clear_sky),
    FEW_CLOUDS(R.string.few_clouds),
    SCATTERED_CLOUDS(R.string.scattered_clouds),
    BROKEN_CLOUDS(R.string.broken_clouds),
    SHOWER_RAIN(R.string.shower_rain),
    THUNDERSTORM(R.string.thunerstorm),
    SNOW(R.string.snow),
    MIST(R.string.mist)
}
