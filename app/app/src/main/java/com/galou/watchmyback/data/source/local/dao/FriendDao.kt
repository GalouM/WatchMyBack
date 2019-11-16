package com.galou.watchmyback.data.source.local.dao

import androidx.room.*
import com.galou.watchmyback.data.entity.Friendship
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.utils.*

/**
 * List all the actions possible on the [Friendship] table
 *
 * @see Dao
 * @see Friendship
 *
 * @author Galou Minisini
 *
 */
@Dao
abstract class FriendDao(private val database: WatchMyBackDatabase) {

    /**
     * Create a [Friendship] object in the database
     *
     * If an object with the same Primary key exist in the database, it will be replace by this one
     *
     * @param friendship [Friendship] object to create
     *
     * @see Insert
     * @see OnConflictStrategy.REPLACE
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun createFriendship(vararg friendship: Friendship)

    /**
     * Delete a [Friendship] object from the database
     *
     * @param friendId ID of the [User] who is the friend
     * @param userId ID of the [User] who own the friend
     *
     * @see Query
     * @see User
     */
    @Query("DELETE FROM $FRIEND_TABLE_NAME WHERE $FRIEND_TABLE_USER_FRIEND_UUID = :friendId " +
            "AND $FRIEND_TABLE_USER_UUID = :userId")
    abstract suspend fun removeFriend(userId: String, friendId: String)

    /**
     * Query all the [User] who are friend with a specific [User]
     *
     * @param userId ID of the [User] to query
     * @return List of [User] the specified [User] is friend with
     *
     * @see Query
     * @see User
     */
    @Query("SELECT * FROM $USER_TABLE_NAME " +
            "INNER JOIN $FRIEND_TABLE_NAME " +
            "ON ${FRIEND_TABLE_NAME}.${FRIEND_TABLE_USER_FRIEND_UUID} = $USER_TABLE_NAME.$USER_TABLE_UUID " +
            "WHERE ${FRIEND_TABLE_NAME}.${FRIEND_TABLE_USER_UUID} = :userId")
    abstract suspend fun getFriendsUser(userId: String): List<User>

    @Transaction
    open suspend fun addFriend(currentUserId: String, vararg friends: User){
        database.userDao().createUser(*friends)
        val friendships = mutableListOf<Friendship>()
        friends.forEach { friend -> friendships.add(Friendship(currentUserId, friend.id)) }
        createFriendship(*friendships.toTypedArray())
    }
}