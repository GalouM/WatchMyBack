package com.galou.watchmyback.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.galou.watchmyback.data.entity.TypePoint
import com.galou.watchmyback.data.entity.WeatherData
import com.galou.watchmyback.utils.*

/**
 * Created by galou on 2019-10-21
 */
@Dao
interface WeatherDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createWeatherData(weatherData: List<WeatherData>)

    /*
    @Query("SELECT * FROM $WEATHER_DATA_TABLE_NAME INNER JOIN $POINT_TRIP_TABLE_NAME ON ${POINT_TRIP_TABLE_NAME}.${POINT_TRIP_UUID} = ${WEATHER_DATA_TABLE_NAME}.${WEATHER_DATA_TABLE_POINT_UUID} WHERE ${POINT_TRIP_TABLE_NAME}.${POINT_TRIP_TRIP_UUID} = :tripId AND ${POINT_TRIP_TABLE_NAME}.${POINT_TRIP_TRIP_UUID} = :typePoint")
    suspend fun getWeatherDataTypePointTrip(tripId: String, typePoint: TypePoint)

     */
}