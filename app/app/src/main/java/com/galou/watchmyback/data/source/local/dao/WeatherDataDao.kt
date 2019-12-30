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
}