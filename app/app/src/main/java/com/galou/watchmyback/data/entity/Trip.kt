package com.galou.watchmyback.data.entity

import androidx.room.*
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TripStatus.*
import com.galou.watchmyback.data.entity.TripType.*
import com.galou.watchmyback.data.entity.TripUpdateFrequency.*
import com.galou.watchmyback.utils.*

/**
 * Represent a Trip of a [User]
 *
 * A [User] can create a trip with different information and assign watchers to it
 * so they will be notify he he'she doesn't come home on time
 *
 * @property id id unique id generated randomly use to identify a trip
 * @property userId ID of the [User] who is doing or did the trip
 * @property checkListId ID of the [CheckList] assigned to this trip
 * @property status status of the trip
 * @property details details of the trip
 * @property updateFrequency frequency at which the app will send location update during the trip
 * @property mainLocation main location of the trip
 * @property type type of trip
 * @property active trip is active or not
 *
 * @see User
 * @see TripStatus
 * @see TripType
 * @see TripUpdateFrequency
 * @see CheckList
 * @see Entity
 *
 * @author Galou Minisini
 */
@Entity(
    tableName = TRIP_TABLE_NAME,
    indices = [
        Index(value = [TRIP_TABLE_STATUS], unique = false),
        Index(value = [TRIP_TABLE_USER_UUID, TRIP_TABLE_ACTIVE], unique = false)
    ],
    foreignKeys = [
    ForeignKey(
        entity = User::class,
        parentColumns = [USER_TABLE_UUID],
        childColumns = [TRIP_TABLE_USER_UUID],
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = CheckList::class,
        parentColumns = [CHECK_LIST_TABLE_UUID],
        childColumns = [TRIP_TABLE_CHECK_LIST_UUID],
        onDelete = ForeignKey.SET_NULL
    )
    ]
)

data class Trip (
    @ColumnInfo(name = TRIP_TABLE_UUID) @PrimaryKey var id: String = idGenerated,
    @ColumnInfo(name = TRIP_TABLE_USER_UUID) var userId: String = "",
    @ColumnInfo(name = TRIP_TABLE_CHECK_LIST_UUID) var checkListId: String? = null,
    @ColumnInfo(name = TRIP_TABLE_STATUS) var status: TripStatus = ON_GOING,
    @ColumnInfo(name = TRIP_TABLE_DETAILS) var details: String = "",
    @ColumnInfo(name = TRIP_TABLE_FREQUENCY) var updateFrequency:TripUpdateFrequency? = null,
    @ColumnInfo(name = TRIP_TABLE_MAIN_LOCATION) var mainLocation: String? = "",
    @ColumnInfo(name = TRIP_TABLE_TYPE) var type: TripType? = null,
    @ColumnInfo(name = TRIP_TABLE_ACTIVE) var active: Boolean = true
)

/**
 * Represent the different type of [Trip] possible
 *
 * @property BIKING a trip by bicycle
 * @property MOUNTAIN_BIKING a mountain biking trip
 * @property HIKING a hiking trip
 * @property RUNNING a running trip
 * @property SKIING a ski trip
 * @property MOTORCYCLE a trip by motorcycle
 *
 * @param typeNameId ID of the String that represent the name of the [TripType]
 *
 * @see Trip
 * @author Galou Minisini
 */
enum class TripType (val typeNameId: Int, val iconId: Int) {
    BIKING(R.string.biking, R.drawable.icon_bike),
    MOUNTAIN_BIKING(R.string.mountain_biking, R.drawable.icon_mtb),
    HIKING(R.string.hiking, R.drawable.icon_hike),
    RUNNING(R.string.running, R.drawable.icon_run),
    SKIING(R.string.skiing, R.drawable.icon_ski),
    MOTORCYCLE(R.string.motorcycle, R.drawable.icon_motorcycle)

}

/**
 * Represent the different frequency of update possible
 *
 *
 * @property NEVER never send updates
 * @property FIFTEEN_MINUTES send updates every 15 minutes
 * @property HALF_HOUR send updates every 30 minutes
 * @property ONE_HOUR send updates every hour
 * @property TWO_HOURS send updates every 2 hours
 *
 * @param frequencyMillisecond frequency in milliseconds
 * @param nameResourceId
 *
 * @see Trip ID of the String that represent the name of the [TripUpdateFrequency]
 *
 * @author Galou Minsini
 *
 */
enum class TripUpdateFrequency(val frequencyMillisecond: Long, val nameResourceId: Int){
    NEVER(0, R.string.never),
    FIFTEEN_MINUTES(900000, R.string.fifteen_minutes),
    HALF_HOUR(1800000, R.string.thirteen_minutes),
    ONE_HOUR(3600000, R.string.hour),
    TWO_HOURS(7200000, R.string.two_hours)
}

/**
 * Represent the different type of status possible for a [Trip]
 *
 * @property ON_GOING trip on going, on time and the [User] is sending location updates
 * @property ON_GOING_NO_NEWS trip on going, seems on time but [User] is not sending location updates
 * @property BACK_SAFE the [User] signal that he/she is back safe
 * @property LATE_EMITTING the [User] is late on his/her schedule but is still emitting his/her position
 * @property LATE_NO_NEWS the [User] is late on his/her schedule and is not emitting his/her position
 * @property REALLY_LATE the [User] is more than an hour late
 * @property DISTRESS the [User] signal that he/she is in distress and emitted his/her location one more time
 *
 * @param statusNameId ID of the String that represent the name of the [TripStatus]
 *
 * @author Galou Minisini
 */
enum class TripStatus(val statusNameId: Int) {
    ON_GOING(R.string.on_schedule),
    ON_GOING_NO_NEWS(R.string.no_updates),
    BACK_SAFE(R.string.back_home),
    LATE_EMITTING(R.string.late),
    LATE_NO_NEWS(R.string.late_no_news),
    REALLY_LATE(R.string.really_late),
    DISTRESS(R.string.distress)
}