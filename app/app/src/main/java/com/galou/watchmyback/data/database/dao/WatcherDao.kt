package com.galou.watchmyback.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.galou.watchmyback.data.entity.Trip
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.Watcher
import com.galou.watchmyback.utils.*

/**
 * Created by galou on 2019-10-21
 */
@Dao
interface WatcherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWatchers(watchers: List<Watcher>)

    @Query("SELECT * FROM $USER_TABLE_NAME " +
            "INNER JOIN $WATCHER_TABLE_NAME " +
            "ON ${WATCHER_TABLE_NAME}.${WATCHER_TABLE_WATCHER_UUID} = $USER_TABLE_NAME.$USER_TABLE_UUID " +
            "WHERE ${WATCHER_TABLE_NAME}.${WATCHER_TABLE_TRIP_UUID} = :tripId")
    suspend fun getWatcherTrip(tripId: String): List<User>

    @Query("SELECT * FROM $TRIP_TABLE_NAME " +
            "INNER JOIN $WATCHER_TABLE_NAME " +
            "ON ${WATCHER_TABLE_NAME}.${WATCHER_TABLE_TRIP_UUID} = ${TRIP_TABLE_NAME}.${TRIP_TABLE_UUID} " +
            "WHERE ${WATCHER_TABLE_NAME}.${WATCHER_TABLE_WATCHER_UUID} = :userId")
    abstract suspend fun getTripUserIsWatching(userId: String): List<Trip>
}