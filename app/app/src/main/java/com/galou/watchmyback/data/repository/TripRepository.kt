package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.TripWithData
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
}