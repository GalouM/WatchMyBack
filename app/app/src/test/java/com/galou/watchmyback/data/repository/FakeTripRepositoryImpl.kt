package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.applicationUse.TripDisplay
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.testHelpers.checkList1
import com.galou.watchmyback.testHelpers.listUsers
import com.galou.watchmyback.testHelpers.tripWithData
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.extension.convertForDisplay

/**
 * @author galou
 * 2019-12-13
 */
class FakeTripRepositoryImpl : TripRepository {

    override var pointSelected: PointTripWithData? = null
    override var tripPoints: List<PointTripWithData>? = null

    override suspend fun createTrip(
        trip: TripWithData,
        checkList: CheckListWithItems?
    ): Result<Void?> = Result.Success(null)

    override suspend fun fetchPointLocationInformation(points: List<PointTripWithData>): Result<Void?> =
        Result.Success(null)

    override suspend fun fetchUserActiveTrip(userId: String, refresh: Boolean): Result<TripWithData> =
        Result.Success(
            TripWithData(
                trip = Trip(active = true, userId = userId, checkListId = checkList1.id),
                points = tripWithData.points,
                watchers = tripWithData.watchers))

    override suspend fun fetchTrip(tripId: String): Result<TripWithData?> {
        val trip = TripWithData(
            trip = Trip(id = tripId, checkListId = checkList1.id),
            points = tripWithData.points,
            watchers = tripWithData.watchers)

        return Result.Success(trip)
    }

    override suspend fun fetchTripUserWatching(userId: String): Result<List<TripWithData>> =
        Result.Success(listOf(tripWithData))

    override suspend fun convertTripForDisplay(
        trips: List<TripWithData>,
        userPrefs: UserPreferences
    ): Result<List<TripDisplay>> {
        val tripForDisplay = mutableListOf<TripDisplay>()
        trips.forEach {trip ->
            val owner = listUsers.find { it.id ==  trip.trip.userId}!!
            tripForDisplay.add(trip.convertForDisplay(userPrefs, owner.username!!))
        }
        return Result.Success(tripForDisplay)
    }
}