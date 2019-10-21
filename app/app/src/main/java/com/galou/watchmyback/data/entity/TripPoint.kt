package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.galou.watchmyback.utils.POINT_TRIP_LOCATION
import com.galou.watchmyback.utils.POINT_TRIP_TRIP
import com.galou.watchmyback.utils.POINT_TRIP_UUID
import com.galou.watchmyback.utils.idGenerated

/**
 * Created by galou on 2019-10-20
 */
abstract class TripPoint(
    @ColumnInfo(name = POINT_TRIP_UUID) @PrimaryKey var uuid: String = idGenerated,
    @ColumnInfo(name = POINT_TRIP_LOCATION) var locationId: String = "",
    @ColumnInfo(name = POINT_TRIP_TRIP) var tripId: String = ""
)