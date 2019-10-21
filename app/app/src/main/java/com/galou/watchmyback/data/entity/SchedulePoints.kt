package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.galou.watchmyback.data.entity.TypePoint.*
import com.galou.watchmyback.utils.*
import java.util.*

/**
 * Created by galou on 2019-10-20
 */
@Entity(
    tableName = SCHEDULE_POINT_TABLE_NAME,
    indices = [Index(value = [SCHEDULE_POINT_TYPE], unique = false)],
    foreignKeys = [
    ForeignKey(
        entity = Trip::class,
        parentColumns = [TRIP_TABLE_UUID],
        childColumns = [POINT_TRIP_TRIP],
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = Location::class,
        parentColumns = [LOCATION_TABLE_UUID],
        childColumns = [POINT_TRIP_LOCATION],
        onDelete = ForeignKey.CASCADE
    )
    ]
)
data class SchedulePoints (
    @ColumnInfo(name = SCHEDULE_POINT_TYPE) var typePoint: TypePoint = STAGE,
    @ColumnInfo(name = SCHEDULE_POINT_SCHEDULE_TIME) var scheduleTime: Date = todaysDate

) : TripPoint()

enum class TypePoint {
    START,
    END,
    STAGE
}