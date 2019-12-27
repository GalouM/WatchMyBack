package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.data.entity.PointTrip
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.TypePoint

/**
 * @author galou
 * 2019-12-01
 */

/**
 * Keep only the [PointTrip] that have the type [TypePoint.SCHEDULE_STAGE] in a list
 *
 * @return mutable list of [PointTripWithData]
 */
fun List<PointTripWithData>.filterScheduleStage(): MutableList<PointTripWithData>{
    return filter { it.pointTrip.typePoint == TypePoint.SCHEDULE_STAGE }.toMutableList()
}

/**
 * Find the first [PointTripWithData] in a list that has the [TypePoint] corresponding
 * If no point is found a new point with the corresponding type is created and added to the list
 *
 * @param typePoint [TypePoint] to find
 * @param tripId the point's trip's ID
 * @return [PointTripWithData] with the corresponding type
 */
fun MutableList<PointTripWithData>.filterOrCreateMainPoint(typePoint: TypePoint, tripId: String): PointTripWithData {
    return when (val point = firstOrNull { it.pointTrip.typePoint == typePoint } ){
        null -> {
            PointTripWithData(
                pointTrip = PointTrip(
                    tripId = tripId,
                    typePoint = typePoint
                )
            ).apply { add(this) }

        }
        else -> point
    }
}

/**
 * Find the most recent check up point in a list of of [PointTripWithData]
 *
 * @return [PointTripWithData] of type [TypePoint.CHECKED_UP] the most recent
 */
fun List<PointTripWithData>.findLatestCheckUpPoint(): PointTripWithData? {
    val checkedUpPoints = filter { it.pointTrip.typePoint == TypePoint.CHECKED_UP }
    return when {
        checkedUpPoints.isEmpty() -> null
        checkedUpPoints.size == 1 -> checkedUpPoints[0]
        else -> checkedUpPoints.maxWith(Comparator { p0, p1 ->
            when {
                p0 === p1 -> 0
                p0.pointTrip.time!!.after(p1.pointTrip.time) -> 1
                p0.pointTrip.time == p1.pointTrip.time -> 0
                else -> -1
            }
        })
    }
}

fun PointTripWithData.getCoordinate(): Coordinate = Coordinate(
    latitude = location?.latitude ?: throw Exception("No latitude for this point $this") ,
    longitude = location.longitude ?: throw Exception("No longitude for this point $this"))

fun PointTripWithData.createMapCoordinate(): Map<String, Coordinate> = mapOf(pointTrip.id to getCoordinate())

