package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.galou.watchmyback.utils.*

/**
 * Created by galou on 2019-10-20
 */
@Entity(
    tableName = WATCHER_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = [USER_TABLE_UUID],
            childColumns = [WATCHER_TABLE_WATCHER_UUID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = [TRIP_TABLE_UUID],
            childColumns = [WATCHER_TABLE_TRIP_UUID],
            onDelete = ForeignKey.CASCADE
        )],
    primaryKeys = [WATCHER_TABLE_WATCHER_UUID, WATCHER_TABLE_TRIP_UUID]
)
data class Watcher(
    @ColumnInfo(name = WATCHER_TABLE_WATCHER_UUID) var watcherId: String = "",
    @ColumnInfo(name = WATCHER_TABLE_TRIP_UUID) var tripId: String = ""
)