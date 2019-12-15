package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.TripWithData
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
}