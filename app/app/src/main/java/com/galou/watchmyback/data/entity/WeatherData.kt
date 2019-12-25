package com.galou.watchmyback.data.entity

import androidx.room.*
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.WeatherCondition.CLEAR
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
    @ColumnInfo(name = WEATHER_DATA_TABLE_CONDITION) var condition: WeatherCondition = CLEAR,
    @ColumnInfo(name = WEATHER_DATA_TABLE_TEMPERATURE) var temperature: Double? = null,
    @ColumnInfo(name = WEATHER_DATA_TABLE_DATETIME) var dateTime: Date? = null,
    @ColumnInfo(name = WEATHER_DATA_TABLE_ICON) var iconName: String = ""
)

/**
 * Represent the different type of weather condition possible
 *
 * @param conditionNameId ID of the String that represent the name of the condition
 *
 * @see WeatherData
 */
enum class WeatherCondition(val conditionNameId: Int, val iconId: Int){
    THUNDERSTORM(R.string.thunerstorm, R.drawable.icon_thunderstorm),
    DRIZZLE(R.string.drizzle, R.drawable.icon_shower_rain),
    RAIN(R.string.rain, R.drawable.icon_rain),
    SNOW(R.string.snow, R.drawable.icon_snow),
    MIST(R.string.mist, R.drawable.icon_mist),
    SMOKE(R.string.smoke, R.drawable.icon_mist),
    HAZE(R.string.Haze, R.drawable.icon_mist),
    DUST(R.string.Dust, R.drawable.icon_mist),
    FOG(R.string.fog, R.drawable.icon_mist),
    SAND(R.string.sand, R.drawable.icon_mist),
    ASH(R.string.ash, R.drawable.icon_mist),
    SQUALL(R.string.squall, R.drawable.icon_mist),
    TORNADO(R.string.tornado, R.drawable.icon_mist),
    CLEAR(R.string.clear, R.drawable.icon_clear_sky),
    CLOUDS(R.string.clouds, R.drawable.icon_few_clouds),
    UNKNOWN(R.string.unknown_condition, R.drawable.icon_clear_sky)
}
