package com.galou.watchmyback.data.source.local.dao

import androidx.room.*
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.utils.POINT_TRIP_TABLE_NAME
import com.galou.watchmyback.utils.POINT_TRIP_TRIP_UUID
import com.galou.watchmyback.utils.POINT_TRIP_TYPE

/**
 * List of all the actions possible on the [PointTrip] table
 *
 * @property database reference to the database of the application
 *
 * @see Dao
 * @see PointTrip
 *
 * @author Galou Minisini
 */
@Dao
abstract class PointTripDao(private val database: WatchMyBackDatabase) {

    /**
     * Create an object [PointTrip] in the database
     *
     * If an object with the same Primary key exist in the database, it will be replace by this one
     *
     * @param pointTrips [PointTrip] object to create
     *
     * @see Insert
     * @see OnConflictStrategy.REPLACE
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun createPoints(vararg pointTrips: PointTrip)

    /**
     * Create a [PointTrip] object and its data in the database
     *
     * @param pointTrips [PointTripWithData] to create
     *
     * @see Transaction
     * @see Location
     * @see WeatherData
     * @see PointTripDao.createPoints
     * @see LocationDao.createLocations
     * @see WeatherDataDao.createWeatherData
     */
    @Transaction
    open suspend fun createPointsAndData(vararg pointTrips: PointTripWithData){
        pointTrips.forEach {
            createPoints(it.pointTrip)
            database.locationDao().createLocations(it.location!!)
            database.weatherDataDao().createWeatherData(it.weatherData!!)
        }
    }

    /**
     * Query all the [PointTrip] objects of a [Trip]
     *
     * @param tripId ID of the [Trip] to query
     * @return List of all the [PointTrip] of the queried [Trip] and their data
     *
     * @see Query
     * @see Transaction
     * @see PointTripWithData
     */
    @Transaction
    @Query("SELECT * FROM $POINT_TRIP_TABLE_NAME WHERE $POINT_TRIP_TRIP_UUID = :tripId")
    abstract suspend fun getAllPointsFromTripWithData(tripId: String): List<PointTripWithData>

    /**
     * Query all the [PointTrip] objects of a [Trip] that have a particular [TypePoint]
     *
     * @param tripId ID of the [Trip] to query
     * @param pointType [TypePoint] to query
     * @return List of all the [PointTrip] of the queried [Trip] and their data
     *
     * @see Transaction
     * @see Query
     * @see TypePoint
     */
    @Transaction
    @Query("SELECT * FROM $POINT_TRIP_TABLE_NAME WHERE $POINT_TRIP_TRIP_UUID = :tripId " +
            "AND $POINT_TRIP_TYPE = :pointType")
    abstract suspend fun getAllPointsFromTripWithDataByType(tripId: String, pointType: TypePoint): List<PointTripWithData>


}