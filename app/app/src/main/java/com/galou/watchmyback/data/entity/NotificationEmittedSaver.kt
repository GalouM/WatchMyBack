package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.galou.watchmyback.utils.*

/**
 * For each trip a user is watching save if notifications has been already sent or not
 *
 * @property tripId ID of the trip watched
 * @property userId Id of the user watching
 * @property lateNotificationEmitted true if a notification about the trip running late has been send
 * @property backSafeNotificationEmitted true is a notification about the tri being done has been send
 */
@Entity(
    tableName = NOTIFICATION_SAVER_TABLE_NAME,
    indices = [
    Index(value = [NOTIFICATION_SAVER_USER_UUID, NOTIFICATION_SAVER_TRIP_UUID], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
        entity = User::class,
        parentColumns = [USER_TABLE_UUID],
        childColumns = [NOTIFICATION_SAVER_USER_UUID],
        onDelete = ForeignKey.CASCADE
    )
    ],
    primaryKeys = [NOTIFICATION_SAVER_USER_UUID, NOTIFICATION_SAVER_TRIP_UUID]
)
data class NotificationEmittedSaver (
    @ColumnInfo(name = NOTIFICATION_SAVER_USER_UUID) val tripId: String,
    @ColumnInfo(name = NOTIFICATION_SAVER_TRIP_UUID) val userId: String,
    @ColumnInfo(name = NOTIFICATION_SAVER_LATE_EMITTED) var lateNotificationEmitted: Boolean = false,
    @ColumnInfo(name = NOTIFICATION_SAVER_BACK_EMITTED) var backSafeNotificationEmitted: Boolean = false
)