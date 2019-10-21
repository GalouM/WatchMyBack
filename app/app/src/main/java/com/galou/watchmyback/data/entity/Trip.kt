package com.galou.watchmyback.data.entity

import androidx.room.*
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TripStatus.ON_GOING
import com.galou.watchmyback.data.entity.TripType.*
import com.galou.watchmyback.data.entity.TripUpdateFrequency.*
import com.galou.watchmyback.utils.*

/**
 * Created by galou on 2019-10-20
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
    @ColumnInfo(name = TRIP_TABLE_UUID) @PrimaryKey var uuid: String = idGenerated,
    @ColumnInfo(name = TRIP_TABLE_USER_UUID) var userId: String = "",
    @ColumnInfo(name = TRIP_TABLE_CHECK_LIST_UUID) var checkList: String? = "",
    @ColumnInfo(name = TRIP_TABLE_STATUS) var status: TripStatus = ON_GOING,
    @ColumnInfo(name = TRIP_TABLE_DETAILS) var details: String = "",
    @ColumnInfo(name = TRIP_TABLE_FREQUENCY) var updateFrequency:TripUpdateFrequency = FIFTEEN_MINUTES,
    @ColumnInfo(name = TRIP_TABLE_MAIN_LOCATION) var mainLocation: String = "",
    @ColumnInfo(name = TRIP_TABLE_TYPE) var type: TripType = BIKING
)

enum class TripType (val typeNameId: Int) {
    BIKING(R.string.biking),
    MOUNTAIN_BIKING(R.string.mountain_biking),
    HIKING(R.string.hiking),
    RUNNING(R.string.running),
    SKIING(R.string.skiing),
    MOTORCYCLE(R.string.motorcycle)

}

enum class TripUpdateFrequency(val frequencyMillisecond: Long){
    FIFTEEN_MINUTES(1),
    HALF_HOUR(2),
    ONE_HOUR(3),
    TWO_HOURS(4)
}

enum class TripStatus(val statusNameId: Int) {
    ON_GOING(R.string.on_schedule),
    ON_GOING_NO_NEWS(R.string.no_updates),
    BACK_SAFE(R.string.back_home),
    LATE_EMITTING(R.string.late),
    LATE_NO_NEWS(R.string.late_no_news),
    REALLY_LATE(R.string.really_late)
}