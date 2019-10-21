package com.galou.watchmyback.data.entity

import androidx.room.*
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TripStatus.ON_GOING
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
 *
 * @see User
 * @see TripStatus
 * @see TripType
 * @see TripUpdateFrequency
 * @see CheckList
 *
 * @author Galou Minisini
 */
@Entity(
    tableName = TRIP_TABLE_NAME,
    indices = [Index(value = [TRIP_TABLE_STATUS], unique = false)],
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
    @ColumnInfo(name = TRIP_TABLE_CHECK_LIST_UUID) var checkListId: String? = "",
    @ColumnInfo(name = TRIP_TABLE_STATUS) var status: TripStatus = ON_GOING,
    @ColumnInfo(name = TRIP_TABLE_DETAILS) var details: String = "",
    @ColumnInfo(name = TRIP_TABLE_FREQUENCY) var updateFrequency:TripUpdateFrequency = FIFTEEN_MINUTES,
    @ColumnInfo(name = TRIP_TABLE_MAIN_LOCATION) var mainLocation: String = "",
    @ColumnInfo(name = TRIP_TABLE_TYPE) var type: TripType = BIKING
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
enum class TripType (val typeNameId: Int) {
    BIKING(R.string.biking),
    MOUNTAIN_BIKING(R.string.mountain_biking),
    HIKING(R.string.hiking),
    RUNNING(R.string.running),
    SKIING(R.string.skiing),
    MOTORCYCLE(R.string.motorcycle)

}

/**
 * Represent the different frequency of update possible
 *
 * @property FIFTEEN_MINUTES send updates every 15 minutes
 * @property HALF_HOUR send updates every 30 minutes
 * @property ONE_HOUR send updates every hour
 * @property TWO_HOURS send updates every 2 hours
 *
 * @param frequencyMillisecond frequency in milliseconds
 *
 * @see Trip
 *
 * @author Galou Minsini
 *
 */
enum class TripUpdateFrequency(val frequencyMillisecond: Long){
    FIFTEEN_MINUTES(1),
    HALF_HOUR(2),
    ONE_HOUR(3),
    TWO_HOURS(4)
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