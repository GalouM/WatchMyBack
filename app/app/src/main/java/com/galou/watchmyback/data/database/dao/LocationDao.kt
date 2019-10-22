package com.galou.watchmyback.data.database.dao

import androidx.room.*
import com.galou.watchmyback.data.database.WatchMyBackDatabase
import com.galou.watchmyback.data.entity.Location
import com.galou.watchmyback.data.entity.WeatherData
import com.galou.watchmyback.utils.LOCATION_TABLE_NAME
import com.galou.watchmyback.utils.LOCATION_TABLE_UUID

/**
 * Created by galou on 2019-10-21
 */
@Dao
interface LocationDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLocations(locations: List<Location>)

}