package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.applicationUse.TripDisplay
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-12-08
 */
interface TripRepository {

    var pointSelected: PointTripWithData?

    var tripPoints: List<PointTripWithData>?

    suspend fun createTrip(trip: TripWithData, checkList: CheckListWithItems?): Result<Void?>

    suspend fun fetchPointLocationInformation(points: List<PointTripWithData>): Result<Void?>

    suspend fun fetchUserActiveTrip(userId: String, refresh: Boolean): Result<TripWithData?>

    suspend fun fetchTrip(tripId: String): Result<TripWithData?>

    suspend fun fetchTripUserWatching(userId: String): Result<List<TripWithData>>

    suspend fun convertTripForDisplay(trips: List<TripWithData>, userPrefs: UserPreferences): Result<List<TripDisplay>>
}