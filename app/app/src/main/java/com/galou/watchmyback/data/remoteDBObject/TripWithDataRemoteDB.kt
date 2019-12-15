package com.galou.watchmyback.data.remoteDBObject

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.utils.*

/**
 * @author galou
 * 2019-12-09
 */

data class TripWithDataRemoteDB(
    @Embedded val trip: Trip = Trip(),
    @Relation(
        parentColumn = TRIP_TABLE_UUID,
        entityColumn = POINT_TRIP_TRIP_UUID,
        entity = PointTrip::class
    )
    val points: MutableList<PointTripWithData> = mutableListOf(),
    @Relation(
        parentColumn = TRIP_TABLE_UUID,
        entityColumn = USER_TABLE_UUID,
        associateBy = Junction(
            value = TripWatcher::class,
            parentColumn = WATCHER_TABLE_TRIP_UUID,
            entityColumn = WATCHER_TABLE_WATCHER_UUID
        ),
        entity = User::class
    )
    var watchersId: List<String> = listOf()
)