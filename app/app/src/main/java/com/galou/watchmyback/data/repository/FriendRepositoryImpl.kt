package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.Friend
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.local.FriendLocalDataSource
import com.galou.watchmyback.data.source.remote.FriendRemoteDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.returnSuccessOrError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * @author galou
 * 2019-11-04
 */
class FriendRepositoryImpl(
    private val localSource: FriendLocalDataSource,
    private val remoteSource: FriendRemoteDataSource
) : FriendRepository {

    override suspend fun addFriend(user: User, friend: User): Result<Void?> = coroutineScope {
        val localTask = async { localSource.addFriend(user, friend) }
        val remoteTask = async { remoteSource.addFriend(user, friend) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }

    override suspend fun removeFriend(user: User, friendId: String): Result<Void?> = coroutineScope {
        val localTask = async { localSource.removeFriend(user, friendId) }
        val remoteTask = async { remoteSource.removeFriend(user, friendId) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }

    override suspend fun fetchUserFriend(user: User, refresh: Boolean): Result<List<User>> {
        if (refresh) {
            val remoteResult = remoteSource.fetchUserFriend(user)
            if (remoteResult is Result.Success) {
                if (remoteResult.data.isNotEmpty()) {
                    localSource.addFriend(user, *remoteResult.data.toTypedArray())
                    return remoteResult
                }
            }
        }
        return localSource.fetchUserFriend(user)
    }
}