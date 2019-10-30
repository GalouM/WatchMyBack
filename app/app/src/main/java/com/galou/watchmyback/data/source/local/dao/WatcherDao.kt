package com.galou.watchmyback.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.galou.watchmyback.data.entity.Trip
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.Watcher
import com.galou.watchmyback.utils.*

/**
 * List all the actions possible on the [WatcherDao] table
 *
 * @see Dao
 * @see Watcher
 *
 */
@Dao
interface WatcherDao {

    /**
     * Create a list of [Watcher] objects in the database
     *
     * If an object with the same Primary key exist in the database, it will be replace by this one
     *
     * @param watchers object to create
     *
     * @see OnConflictStrategy.REPLACE
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWatchers(watchers: List<Watcher>)

    /**
     * Query all the [Watcher] of a [Trip]
     *
     * @param tripId ID of the trip to query
     * @return List of [User] watching this trip
     *
     * @see Query
     * @see Trip
     * @see User
     */
    @Query("SELECT * FROM $USER_TABLE_NAME " +
            "INNER JOIN $WATCHER_TABLE_NAME " +
            "ON ${WATCHER_TABLE_NAME}.${WATCHER_TABLE_WATCHER_UUID} = $USER_TABLE_NAME.$USER_TABLE_UUID " +
            "WHERE ${WATCHER_TABLE_NAME}.${WATCHER_TABLE_TRIP_UUID} = :tripId")
    suspend fun getWatcherTrip(tripId: String): List<User>

    /**
     * Query all the [Trip] a user is watching
     *
     * @param userId ID of the user to query
     * @return List of [Trip] this user is watching
     *
     * @see Query
     * @see Trip
     */
    @Query("SELECT * FROM $TRIP_TABLE_NAME " +
            "INNER JOIN $WATCHER_TABLE_NAME " +
            "ON ${WATCHER_TABLE_NAME}.${WATCHER_TABLE_TRIP_UUID} = ${TRIP_TABLE_NAME}.${TRIP_TABLE_UUID} " +
            "WHERE ${WATCHER_TABLE_NAME}.${WATCHER_TABLE_WATCHER_UUID} = :userId")
    suspend fun getTripUserIsWatching(userId: String): List<Trip>
}