package com.galou.watchmyback.data.source.local.dao

import androidx.room.*
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.utils.PREFERENCES_TABLE_NAME
import com.galou.watchmyback.utils.PREFERENCES_TABLE_USER_UUID

/**
 * List of all the possible action on the [UserPreferences] table
 *
 * @see UserPreferences
 * @see Dao
 *
 */
@Dao
interface UserPreferencesDao {

    /**
     * Create a object [UserPreferences] in the database
     *
     * @param preferences preferences to create
     *
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUserPreferences(preferences: UserPreferences)

    /**
     * Query the preferences of a specific user
     *
     * @param userId ID of the user tho whom we are requested the preferences
     */
    @Query("SELECT * FROM $PREFERENCES_TABLE_NAME WHERE $PREFERENCES_TABLE_USER_UUID = :userId")
    suspend fun getUserPreferences(userId: String): UserPreferences?

    /**
     * Update a [UserPreferences] object in the database
     *
     * @param preferences [UserPreferences] to update
     */
    @Update
    suspend fun updateUserPreferences(preferences: UserPreferences)

    /**
     * Delete a [UserPreferences] object in the database
     *
     * @param preferences [UserPreferences] to delete
     *
     * @see Delete
     */
    @Delete
    suspend fun deleteUserPreferences(preferences: UserPreferences)
}