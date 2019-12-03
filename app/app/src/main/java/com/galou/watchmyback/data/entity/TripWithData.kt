package com.galou.watchmyback.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.galou.watchmyback.utils.*

/**
 * @author galou
 * 2019-11-29
 */
data class TripWithData(
    @Embedded val trip: Trip,
    @Relation(
        parentColumn = TRIP_TABLE_UUID,
        entityColumn = POINT_TRIP_TRIP_UUID,
        entity = PointTrip::class
    )
    val points: MutableList<PointTripWithData>,
    @Relation(
        parentColumn = TRIP_TABLE_UUID,
        entityColumn = USER_TABLE_UUID,
        associateBy = Junction(
            value = Watcher::class,
            parentColumn = WATCHER_TABLE_TRIP_UUID,
            entityColumn = WATCHER_TABLE_WATCHER_UUID
        ),
        entity = User::class
    )
    var watchers: List<User>
)