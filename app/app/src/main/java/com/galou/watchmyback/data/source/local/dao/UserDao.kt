package com.galou.watchmyback.data.source.local.dao

import androidx.room.*
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.entity.UserWithPreferences
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.utils.USER_TABLE_EMAIL
import com.galou.watchmyback.utils.USER_TABLE_NAME
import com.galou.watchmyback.utils.USER_TABLE_USERNAME
import com.galou.watchmyback.utils.USER_TABLE_UUID

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
abstract class UserDao(private val database: WatchMyBackDatabase) {

    /**
     * Query a [User] with a specific ID
     *
     * @param userId ID to query
     * @return [User] object with the ID requested
     *
     * @see Query
     */
    @Query("SELECT * FROM $USER_TABLE_NAME WHERE $USER_TABLE_UUID = :userId")
    abstract suspend fun getUser(userId: String): User?

    /**
     * Query all the [User]
     *
     * @return List of [User]
     *
     * @see Query
     */
    @Query("SELECT * FROM $USER_TABLE_NAME")
    abstract suspend fun getAllUsers(): List<User>

    @Transaction
    @Query("SELECT * FROM $USER_TABLE_NAME WHERE $USER_TABLE_UUID = :userId")
    abstract suspend fun getUserWithPreferences(userId: String): UserWithPreferences?

    /**
     * Query a list of [User] who have a specific chain of character in their username
     *
     * @param username chain of character to query
     * @return List of [User] wo have the chain of character requested in their username
     *
     * @see Query
     */
    @Query("SELECT * FROM $USER_TABLE_NAME WHERE $USER_TABLE_USERNAME LIKE :username")
    abstract suspend fun getUsersFromUsername(username: String): List<User>

    /**
     * Query a list of [User] who have a specific chain of character in their email address
     *
     * @param emailAddress chain of character to query
     * @return List of [User] wo have the chain of character requested in their email address
     *
     * @see Query
     */
    @Query("SELECT * FROM $USER_TABLE_NAME WHERE $USER_TABLE_EMAIL LIKE :emailAddress")
    abstract suspend fun getUsersFromEmail(emailAddress: String): List<User>

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
    abstract suspend fun createUser(vararg user: User)


    /**
     * Update the [User] in the database
     *
     * @param user [User to Update]
     *
     * @see Update
     */
    @Update
    abstract suspend fun updateUser(user: User)

    /**
     * Delete [User] from the database
     *
     * @param user user to delete
     *
     * @see Delete
     */
    @Delete
    abstract suspend fun deleteUser(user: User)

    @Transaction
    open suspend fun createOrUpdateUserWithData(createUser: Boolean, user: User, preferences: UserPreferences, vararg friend: User){
        if(createUser){
            createUser(user)
            database.userPreferencesDao().createUserPreferences(preferences)
        } else {
            updateUser(user)
        }
        database.friendDao().addFriend(user.id, *friend)

    }


}