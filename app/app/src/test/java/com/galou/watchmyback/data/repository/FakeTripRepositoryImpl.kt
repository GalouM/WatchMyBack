package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.Trip
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.testHelpers.checkList1
import com.galou.watchmyback.testHelpers.tripWithData
import com.galou.watchmyback.utils.Result

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

    override suspend fun fetchTripUserWatching(userId: String): Result<List<TripWithData>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}