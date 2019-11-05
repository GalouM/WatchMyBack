package com.galou.watchmyback.data.source

import com.galou.watchmyback.data.entity.Friend
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-11-04
 */
interface FriendDataSource {

    suspend fun addFriend(friend: Friend): Result<Void?>

    suspend fun removeFriend(userId: String, friendId:String): Result<Void?>

    suspend fun fetchUserFriend(userId: String): Result<List<User>>
}