package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.Friend
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-11-04
 */
interface FriendRepository {

    suspend fun addFriend(user: User, friend: User): Result<Void?>

    suspend fun removeFriend(user: User, friendId: String): Result<Void?>

    suspend fun fetchUserFriend(user: User, refresh: Boolean): Result<List<User>>
}