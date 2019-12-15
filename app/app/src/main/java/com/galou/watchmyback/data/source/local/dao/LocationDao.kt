package com.galou.watchmyback.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.galou.watchmyback.data.entity.Location

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
    suspend fun createLocations(vararg locations: Location)

}