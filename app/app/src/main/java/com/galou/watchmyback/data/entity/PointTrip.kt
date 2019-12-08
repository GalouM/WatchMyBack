package com.galou.watchmyback.data.entity

import androidx.room.*
import com.galou.watchmyback.data.entity.TypePoint.*
import com.galou.watchmyback.utils.*
import java.util.*

/**
 * Represent a point of the trip.
 *
 * It can be a point where the user is planning to go through or a point by which he/she went by.
 *
 *
 * @property id unique id generated randomly use to identify a point
 * @property tripId ID of the trip to which belongs this point
 * @property typePoint type of point, Start Point, End Point or Stage
 * @property time time the [User] thinks he/she will go through this point
 *
 * @see User
 * @see TypePoint
 * @see Entity
 *
 * @author Galou Minisini
 */
@Entity(
    tableName = POINT_TRIP_TABLE_NAME,
    indices = [
        Index(value = [POINT_TRIP_TYPE], unique = false),
        Index(value = [POINT_TRIP_TRIP_UUID], unique = false)
    ],
    foreignKeys = [
    ForeignKey(
        entity = Trip::class,
        parentColumns = [TRIP_TABLE_UUID],
        childColumns = [POINT_TRIP_TRIP_UUID],
        onDelete = ForeignKey.CASCADE
    )
    ]
)
data class PointTrip (
    @ColumnInfo(name = POINT_TRIP_UUID) @PrimaryKey var id: String = idGenerated,
    @ColumnInfo(name = POINT_TRIP_TRIP_UUID) var tripId: String = "",
    @ColumnInfo(name = POINT_TRIP_TYPE) var typePoint: TypePoint = SCHEDULE_STAGE,
    @ColumnInfo(name = POINT_TRIP_TIME) var time: Date? = null

)


/**
 * Represent the different types of [PointTrip] possible
 *
 * @property START Start point of the trip
 * @property END End point of the trip
 * @property SCHEDULE_STAGE a point by which the user is planning to go through
 * @property CHECKED_UP a point by which the user went through.
 *
 * @see Trip
 * @see PointTrip
 *
 * @author Galou Minisini
 */
enum class TypePoint(val typeName: String) {
    START("start"),
    END("end"),
    SCHEDULE_STAGE("schedule_stage"),
    CHECKED_UP("checked_up")
}