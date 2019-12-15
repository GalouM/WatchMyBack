package com.galou.watchmyback.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.galou.watchmyback.data.entity.WeatherData

/**
 * List all the actions possible on the [WeatherData] table
 *
 * @see WeatherData
 * @see Dao
 *
 * @author Galou Minisini
 *
 */
@Dao
interface WeatherDataDao {

    /**
     * Create a [WeatherData] object in the database
     *
     * If an object with the same Primary key exist in the database, it will be replace by this one
     *
     * @param weatherData object to create
     *
     * @see OnConflictStrategy.REPLACE
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createWeatherData(vararg weatherData: WeatherData)

    /*
    @Query("SELECT * FROM $WEATHER_DATA_TABLE_NAME INNER JOIN $POINT_TRIP_TABLE_NAME ON ${POINT_TRIP_TABLE_NAME}.${POINT_TRIP_UUID} = ${WEATHER_DATA_TABLE_NAME}.${WEATHER_DATA_TABLE_POINT_UUID} WHERE ${POINT_TRIP_TABLE_NAME}.${POINT_TRIP_TRIP_UUID} = :tripId AND ${POINT_TRIP_TABLE_NAME}.${POINT_TRIP_TRIP_UUID} = :typePoint")
    suspend fun getWeatherDataTypePointTrip(tripId: String, typePoint: TypePoint)

     */
}