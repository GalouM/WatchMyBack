package com.galou.watchmyback.data.source

import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-11-04
 */
interface FriendDataSource {

    suspend fun addFriend(user: User, vararg friend: User): Result<Void?>

    suspend fun removeFriend(user: User, friendId:String): Result<Void?>

    suspend fun fetchUserFriend(user: User): Result<List<User>>
}