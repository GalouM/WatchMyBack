package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.galou.watchmyback.data.entity.TypePoint.*
import com.galou.watchmyback.utils.*
import java.util.*

/**
 * Represent a point on the map by which the user planned to go through.
 *
 * Inherit from [TripPoint]
 *
 *
 * @property typePoint type of point, Start Point, End Point or Stage
 * @property scheduleTime time the [User] thinks he/she will go through this point
 *
 * @see TripPoint
 * @see User
 * @see TypePoint
 *
 * @author Galou Minisini
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
    )
    ]
)
data class SchedulePoint (
    @ColumnInfo(name = SCHEDULE_POINT_TYPE) var typePoint: TypePoint = STAGE,
    @ColumnInfo(name = SCHEDULE_POINT_SCHEDULE_TIME) var scheduleTime: Date = todaysDate

) : TripPoint()


/**
 * Represent the different types of [SchedulePoint] possible
 *
 * @property START Start point of the trip
 * @property END End point of the trip
 * @property STAGE a stage point of the trip
 *
 * @see Trip
 * @see SchedulePoint
 *
 * @author Galou Minisini
 */
enum class TypePoint(val typeName: String) {
    START("start"),
    END("end"),
    STAGE("stage")
}