package com.galou.watchmyback.data.source.local.dao

import androidx.room.*
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.data.entity.Trip
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.utils.TRIP_TABLE_ACTIVE
import com.galou.watchmyback.utils.TRIP_TABLE_NAME
import com.galou.watchmyback.utils.TRIP_TABLE_USER_UUID
import com.galou.watchmyback.utils.TRIP_TABLE_UUID
import com.galou.watchmyback.utils.extension.toTripWatchers

/**
 * List all the actions possible on the [Trip] table
 *
 * @property database reference to the database of the application
 *
 * @see Dao
 * @see Trip
 */
@Dao
abstract class TripDao(private val database: WatchMyBackDatabase) {

    /**
     * Query the active [Trip] of a specific [User]
     *
     * @param userId ID of the user to query
     * @return the last active [Trip] of the [User]
     *
     * @see Query
     */
    @Query("SELECT * FROM $TRIP_TABLE_NAME " +
            "WHERE $TRIP_TABLE_USER_UUID = :userId " +
            "AND $TRIP_TABLE_ACTIVE = 1")
    abstract suspend fun getUserActiveTrip(userId: String): TripWithData?

    /**
     * Fetch a [TripWithData] by id
     *
     * @param tripId Id of the trip
     * @return [TripWithData] with the corresponding id
     *
     * @see Query
     */
    @Query("SELECT * FROM $TRIP_TABLE_NAME " +
            "WHERE $TRIP_TABLE_UUID = :tripId")
    abstract suspend fun getTrip(tripId: String): TripWithData?

    /**
     * Create an object [Trip] in the database
     *
     * If an object with the same Primary key exist in the database, it will be replace by this one
     *
     * @param trip [Trip to create]
     *
     * @see Insert
     * @see OnConflictStrategy.REPLACE
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun createTrip(trip: Trip)

    /*

    @Query("DELETE FROM $TRIP_TABLE_NAME INNER JOIN $POINT_TRIP_TABLE_NAME ON ${POINT_TRIP_TABLE_NAME}.${POINT_TRIP_TRIP_UUID} = ${TRIP_TABLE_NAME}.${TRIP_TABLE_UUID} WHERE ${POINT_TRIP_TABLE_NAME}.${POINT_TRIP_TYPE} = :typePointSelected AND ${POINT_TRIP_TABLE_NAME}.${POINT_TRIP_TIME} >= :limitDate")
    abstract suspend fun deleteOldTrips(typePointSelected: TypePoint, limitDate: Date)

     */

    /**
     * Delete all the user's trips that are active
     *
     * @param userId id of the user
     *
     * @see Query
     */
    @Query("DELETE FROM $TRIP_TABLE_NAME WHERE $TRIP_TABLE_USER_UUID =:userId AND $TRIP_TABLE_ACTIVE = 1")
    abstract suspend fun deleteActiveTrips(userId: String)

    /**
     * Create a [Trip] and all its data in the database
     *
     * @param trip [TripWithData] to create
     * @param items List of the [ItemCheckList] took in this [Trip]
     *
     * @see Transaction
     * @see PointTripDao
     * @see ItemCheckListDao
     * @see WatcherDao
     * @see ItemCheckListDao.updateItems
     * @see TripDao.createTrip
     * @see WatcherDao.addWatchers
     * @see PointTripDao.createPointsAndData
     */
    @Transaction
    open suspend fun createTripAndData(
        trip: TripWithData, items: List<ItemCheckList>?
    ){
        items?.let { database.itemCheckListDao().updateItems(it) }
        createTrip(trip.trip)
        database.watcherDao().addWatchers(trip.watchers toTripWatchers trip.trip.id)
        database.pointTripDao().createPointsAndData(trip.points)

    }


}