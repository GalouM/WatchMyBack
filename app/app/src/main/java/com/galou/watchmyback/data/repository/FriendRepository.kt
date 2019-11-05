package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.Friend
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-11-04
 */
interface FriendRepository {

    suspend fun addFriend(userId: String, friendId: String): Result<Void?>

    suspend fun removeFriend(userId: String, friendId:String): Result<Void?>

    suspend fun fetchUserFriend(userId: String): Result<List<User>>
}