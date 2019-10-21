package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.galou.watchmyback.utils.POINT_TRIP_LOCATION
import com.galou.watchmyback.utils.POINT_TRIP_TRIP
import com.galou.watchmyback.utils.POINT_TRIP_UUID
import com.galou.watchmyback.utils.idGenerated

/**
 * Abstract class represent a Point of the [Trip]
 *
 * @property uuid unique id generated randomly use to identify a point
 * @property locationId ID of the [Location] data of the point
 * @property tripId ID of the trip to which belongs this point
 *
 * @see Trip
 * @see StagePoint
 * @see SchedulePoint
 * @see Location
 *
 * @author Galou Minisini
 */
abstract class TripPoint(
    @ColumnInfo(name = POINT_TRIP_UUID) @PrimaryKey var uuid: String = idGenerated,
    @ColumnInfo(name = POINT_TRIP_LOCATION) var locationId: String = "",
    @ColumnInfo(name = POINT_TRIP_TRIP) var tripId: String = ""
)