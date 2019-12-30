package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.data.applicationUse.TripDisplay
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-12-08
 */
interface TripRepository {

    var pointSelected: PointTripWithData?

    var tripPoints: List<PointTripWithData>?

    suspend fun createTrip(trip: TripWithData, checkList: CheckListWithItems?): Result<Void?>

    suspend fun fetchPointLocationInformation(vararg points: PointTripWithData): Result<Void?>

    suspend fun fetchUserActiveTrip(userId: String, refresh: Boolean): Result<TripWithData?>

    suspend fun fetchTrip(tripId: String): Result<TripWithData?>

    suspend fun fetchTripUserWatching(userId: String): Result<List<TripWithData>>

    suspend fun convertTripForDisplay(trips: List<TripWithData>, userPrefs: UserPreferences): Result<List<TripDisplay>>

    suspend fun updateTripStatus(trip: TripWithData): Result<Void?>

    suspend fun createCheckUpPoint(currentUserId: String, coordinate: Coordinate): Result<Void?>

    suspend fun updatePointsTrip(trip: TripWithData): Result<Void?>

    suspend fun fetchNotificationEmittedSaver(userId: String, tripId: String): Result<NotificationEmittedSaver?>

    suspend fun updateNotificationSaver(notificationEmittedSaver: NotificationEmittedSaver): Result<Void?>

    suspend fun createNotificationSaver(userId: String, tripId: String): Result<Void?>
}