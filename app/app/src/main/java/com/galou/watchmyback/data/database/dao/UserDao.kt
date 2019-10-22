package com.galou.watchmyback.data.database.dao

import androidx.room.*
import com.galou.watchmyback.data.database.WatchMyBackDatabase
import com.galou.watchmyback.data.entity.Trip
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.*

/**
 * List all the actions possible on the [User] table
 *
 * @see Dao
 * @see User
 *
 * @author Galou Minisini
 *
 */
@Dao
interface UserDao {

    /**
     * Query a [User] with a specific ID
     *
     * @param userId ID to query
     * @return [User] object with the ID requested
     *
     * @see Query
     */
    @Query("SELECT * FROM $USER_TABLE_NAME WHERE $USER_TABLE_UUID = :userId")
    suspend fun getUser(userId: String): List<User>

    /**
     * Query a list of [User] who have a specific chain of character in their username
     *
     * @param username chain of character to query
     * @return List of [User] wo have the chain of character requested in their username
     *
     * @see Query
     */
    @Query("SELECT * FROM $USER_TABLE_NAME WHERE $USER_TABLE_USERNAME LIKE :username")
    suspend fun getUsersFromUsername(username: String): List<User>

    /**
     * Create a [User] object in the database
     *
     * If an object with the same Primary key exist in the database, it will be replace by this one
     *
     * @param user [User] to create
     *
     * @see OnConflictStrategy.REPLACE
     * @see Insert
     *
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUser(user: User)


    /**
     * Update the [User] in the database
     *
     * @param user [User to Update]
     *
     * @see Update
     */
    @Update
    suspend fun updateUser(user: User)

    /**
     * Delete [User] with a specific ID from the database
     *
     * @param userId ID of the user to delete
     *
     * @see Query
     */
    @Query("DELETE FROM $USER_TABLE_NAME WHERE $USER_TABLE_UUID = :userId")
    suspend fun deleteUser(userId: String)

}