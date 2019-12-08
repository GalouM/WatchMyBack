package com.galou.watchmyback.data.source.local.dao

import androidx.room.*
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.utils.TRIP_TABLE_ACTIVE
import com.galou.watchmyback.utils.TRIP_TABLE_NAME
import com.galou.watchmyback.utils.TRIP_TABLE_USER_UUID

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
     * Query all the active [Trip] of a specific [User]
     *
     * @param userId ID of the user to query
     * @return List of the active [Trip] of the [User]
     *
     * @see Query
     */
    @Query("SELECT * FROM $TRIP_TABLE_NAME " +
            "WHERE $TRIP_TABLE_USER_UUID = :userId " +
            "AND $TRIP_TABLE_ACTIVE = 1")
    abstract suspend fun getUserActiveTrip(userId: String): TripWithData

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
     * Create a [Trip] and all its data in the database
     *
     * @param trip [Trip] to create
     * @param pointTrip List of all the [PointTrip] of the [Trip]
     * @param pointLocations List of the [Location] of the [PointTrip]
     * @param weatherData List of the [WeatherData] of the [PointTrip]
     * @param tripWatchers List of the [TripWatcher] of the [Trip]
     * @param items List of the [ItemCheckList] took in this [Trip]
     *
     * @see Transaction
     * @see PointTripDao
     * @see LocationDao
     * @see WeatherDataDao
     * @see ItemCheckListDao
     * @see WatcherDao
     * @see ItemCheckListDao.updateItems
     * @see TripDao.createTrip
     * @see WatcherDao.addWatchers
     * @see PointTripDao.createPointsAndData
     */
    @Transaction
    open suspend fun createTripAndData(
        trip: Trip, pointTrip: List<PointTrip>, pointLocations: List<Location>,
        weatherData: List<WeatherData>, tripWatchers: List<TripWatcher>, items: List<ItemCheckList>
    ){
        database.itemCheckListDao().updateItems(items)
        createTrip(trip)
        database.watcherDao().addWatchers(tripWatchers)
        database.pointTripDao().createPointsAndData(pointTrip, pointLocations, weatherData)

    }


}