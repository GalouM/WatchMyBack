package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.testHelpers.firstFriend
import com.galou.watchmyback.testHelpers.secondFriend
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-11-05
 */
class FakeFriendRepositoryImpl : FriendRepository {

    override suspend fun addFriend(user: User, friend: User): Result<Void?> = Result.Success(null)

    override suspend fun removeFriend(user: User, friendId: String): Result<Void?> = Result.Success(null)

    override suspend fun fetchUserFriend(user: User, refresh: Boolean): Result<List<User>> = Result.Success(
        listOf(firstFriend, secondFriend))
}