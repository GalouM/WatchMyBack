package com.galou.watchmyback.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.galou.watchmyback.data.entity.Friend
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.*

/**
 * Created by galou on 2019-10-21
 */
@Dao
interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFriend(friend: Friend)

    @Query("DELETE FROM $FRIEND_TABLE_NAME WHERE $FRIEND_TABLE_USER_FRIEND_UUID = :friendId " +
            "AND $FRIEND_TABLE_USER_UUID = :userId")
    suspend fun removeFriend(friendId: String, userId: String)

    @Query("SELECT * FROM $USER_TABLE_NAME " +
            "INNER JOIN $FRIEND_TABLE_NAME " +
            "ON ${FRIEND_TABLE_NAME}.${FRIEND_TABLE_USER_FRIEND_UUID} = $USER_TABLE_NAME.$USER_TABLE_UUID " +
            "WHERE ${FRIEND_TABLE_NAME}.${FRIEND_TABLE_USER_UUID} = :userId")
    suspend fun getFriendsUser(userId: String): List<User>
}