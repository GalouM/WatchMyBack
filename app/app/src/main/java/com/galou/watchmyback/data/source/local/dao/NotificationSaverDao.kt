package com.galou.watchmyback.data.source.local.dao

import androidx.room.*
import com.galou.watchmyback.data.entity.NotificationEmittedSaver
import com.galou.watchmyback.utils.NOTIFICATION_SAVER_TABLE_NAME
import com.galou.watchmyback.utils.NOTIFICATION_SAVER_TRIP_UUID
import com.galou.watchmyback.utils.NOTIFICATION_SAVER_USER_UUID

/***
 * List all the possible actions on [NotificationEmittedSaver] table
 */
@Dao
interface NotificationSaverDao {

    /**
     * Insert a [NotificationEmittedSaver] object in the database
     * If the object already exist  it will not be created
     *
     * @param notificationEmittedSaver object to create
     *
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createNotificationSaver(notificationEmittedSaver: NotificationEmittedSaver)

    /**
     * Update a [NotificationEmittedSaver] object in the database
     *
     * @param notificationEmittedSaver object to update
     *
     * @see Update
     */
    @Update
    suspend fun updateNotificationSaver(notificationEmittedSaver: NotificationEmittedSaver)

    /**
     * Get a [NotificationEmittedSaver] object from the database that has a specific user ID and trip ID
     *
     * @param userId
     * @param tripId
     * @return
     */
    @Query("SELECT * FROM $NOTIFICATION_SAVER_TABLE_NAME " +
            "WHERE $NOTIFICATION_SAVER_USER_UUID = :userId " +
            "AND $NOTIFICATION_SAVER_TRIP_UUID = :tripId")
    suspend fun fetchNotificationForTrip(userId: String, tripId: String): NotificationEmittedSaver?

}