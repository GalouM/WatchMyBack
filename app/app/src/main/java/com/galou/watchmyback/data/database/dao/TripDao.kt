package com.galou.watchmyback.data.database.dao

import androidx.room.*
import com.galou.watchmyback.data.database.WatchMyBackDatabase
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.utils.*
import java.util.*

/**
 * Created by galou on 2019-10-21
 */
@Dao
abstract class TripDao(private val database: WatchMyBackDatabase) {

    @Query("SELECT * FROM $TRIP_TABLE_NAME " +
            "WHERE $TRIP_TABLE_USER_UUID = :userId " +
            "AND $TRIP_TABLE_ACTIVE = :active")
    abstract suspend fun getUserActiveTrip(userId: String, active: Boolean): List<Trip>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun createTrip(trip: Trip)

    /*

    @Query("DELETE FROM $TRIP_TABLE_NAME INNER JOIN $POINT_TRIP_TABLE_NAME ON ${POINT_TRIP_TABLE_NAME}.${POINT_TRIP_TRIP_UUID} = ${TRIP_TABLE_NAME}.${TRIP_TABLE_UUID} WHERE ${POINT_TRIP_TABLE_NAME}.${POINT_TRIP_TYPE} = :typePointSelected AND ${POINT_TRIP_TABLE_NAME}.${POINT_TRIP_TIME} >= :limitDate")
    abstract suspend fun deleteOldTrips(typePointSelected: TypePoint, limitDate: Date)

     */

    @Transaction
    open suspend fun createTripAndData(
        trip: Trip, pointTrip: List<PointTrip>, pointLocations: List<Location>,
        weatherData: List<WeatherData>, watchers: List<Watcher>, items: List<ItemCheckList>
    ){
        database.itemCheckListDao().updateItems(items)
        createTrip(trip)
        database.watcherDao().addWatchers(watchers)
        database.pointTripDao().createPointsAndData(pointTrip, pointLocations, weatherData)

    }


}