package com.galou.watchmyback.data.database.dao

import androidx.room.*
import com.galou.watchmyback.data.database.WatchMyBackDatabase
import com.galou.watchmyback.data.entity.Location
import com.galou.watchmyback.data.entity.WeatherData
import com.galou.watchmyback.utils.LOCATION_TABLE_NAME
import com.galou.watchmyback.utils.LOCATION_TABLE_UUID

/**
 * List of all tha actions possible on the [Location] table
 *
 * @see Dao
 * @see Location
 *
 * @author Galou Minisini
 *
 */
@Dao
interface LocationDao{

    /**
     * Create an [Location] object in the database
     *
     * If an object with the same Primary key exist in the database, it will be replace by this one
     *
     * @param locations [Location] object to create
     *
     * @see Insert
     * @see OnConflictStrategy.REPLACE
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLocations(locations: List<Location>)

}