package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.galou.watchmyback.utils.*

/**
 * Represent a Watcher of a [Trip]
 *
 * A Watcher is a [User] who can see the [Trip]'s information of a another user and is notify if something happen
 *
 * This is Junction table to connect a [User] to a [Trip]
 *
 * @property watcherId ID of the [User] watching the trip
 * @property tripId ID of the [Trip] the watcher is watching
 *
 * @see User
 * @see Trip
 * @see Entity
 *
 * @author Galou Minisini
 */
@Entity(
    tableName = WATCHER_TABLE_NAME,
    indices = [Index(value = [WATCHER_TABLE_WATCHER_UUID, WATCHER_TABLE_TRIP_UUID], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = [USER_TABLE_UUID],
            childColumns = [WATCHER_TABLE_WATCHER_UUID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Trip::class,
            parentColumns = [TRIP_TABLE_UUID],
            childColumns = [WATCHER_TABLE_TRIP_UUID],
            onDelete = ForeignKey.CASCADE
        )],
    primaryKeys = [WATCHER_TABLE_WATCHER_UUID, WATCHER_TABLE_TRIP_UUID]
)
data class TripWatcher(
    @ColumnInfo(name = WATCHER_TABLE_WATCHER_UUID) var watcherId: String = "",
    @ColumnInfo(name = WATCHER_TABLE_TRIP_UUID) var tripId: String = ""
)