package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.galou.watchmyback.utils.*
import java.util.*

/**
 * Represent a point on the map by which the user went through.
 *
 * Inherit from [TripPoint]
 *
 * @property timeWentThrough time at which the [User] went through this point
 *
 * @see TripPoint
 * @see User
 * @see TypePoint
 *
 * @author Galou Minisini
 */
@Entity(
    tableName = STAGE_POINT_TABLE_NAME,
    indices = [Index(value = [SCHEDULE_POINT_TYPE], unique = false)],
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = [TRIP_TABLE_UUID],
            childColumns = [POINT_TRIP_TRIP],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StagePoint(
    @ColumnInfo(name = STAGE_POINT_TIME) var timeWentThrough: Date = todaysDate
)