package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.galou.watchmyback.utils.*
import java.util.*

/**
 * Created by galou on 2019-10-20
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
        ),
        ForeignKey(
            entity = Location::class,
            parentColumns = [LOCATION_TABLE_UUID],
            childColumns = [POINT_TRIP_LOCATION],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StagePoint(
    @ColumnInfo(name = STAGE_POINT_TIME) var timeWentThrough: Date = todaysDate
)