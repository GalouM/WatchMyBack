package com.galou.watchmyback.data.database.dao

import androidx.room.*
import com.galou.watchmyback.data.database.WatchMyBackDatabase
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.utils.POINT_TRIP_TABLE_NAME
import com.galou.watchmyback.utils.POINT_TRIP_TRIP_UUID
import com.galou.watchmyback.utils.POINT_TRIP_TYPE

/**
 * Created by galou on 2019-10-21
 */

@Dao
abstract class PointTripDao(private val database: WatchMyBackDatabase) {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun createPoints(pointTrips: List<PointTrip>)

    @Transaction
    open suspend fun createPointsAndData(
        pointTrips: List<PointTrip>, locations: List<Location>, weatherData: List<WeatherData>
    ){
        createPoints(pointTrips)
        database.locationDao().createLocations(locations)
        database.weatherDataDao().createWeatherData(weatherData)
    }

    @Transaction
    @Query("SELECT * FROM $POINT_TRIP_TABLE_NAME WHERE $POINT_TRIP_TRIP_UUID = :tripId")
    abstract suspend fun getAllPointsFromTripWithData(tripId: String): List<PointTripWithData>

    @Transaction
    @Query("SELECT * FROM $POINT_TRIP_TABLE_NAME WHERE $POINT_TRIP_TRIP_UUID = :tripId " +
            "AND $POINT_TRIP_TYPE = :pointType")
    abstract suspend fun getAllPointsFromTripWithDataByType(tripId: String, pointType: TypePoint): List<PointTripWithData>


}