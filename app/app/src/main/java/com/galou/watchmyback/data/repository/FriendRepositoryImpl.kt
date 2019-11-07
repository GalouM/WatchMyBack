package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.OtherUser
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.local.FriendLocalDataSource
import com.galou.watchmyback.data.source.remote.FriendRemoteDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.extension.toListOtherUser
import com.galou.watchmyback.utils.returnSuccessOrError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

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

    override suspend fun fetchUserFriend(user: User, refresh: Boolean): Result<List<OtherUser>> {
        when (refresh) {
            true -> {
                val remoteResult = remoteSource.fetchUserFriend(user)
                if (remoteResult is Result.Success) {
                    if (remoteResult.data.isNotEmpty()) {
                        localSource.addFriend(user, *remoteResult.data.toTypedArray())
                        return Result.Success(remoteResult.data.toListOtherUser(true))
                    }
                }
            }
        }
        return when (val localResult = localSource.fetchUserFriend(user)) {
            is Result.Success -> Result.Success(localResult.data.toListOtherUser(true))
            is Result.Error -> Result.Error(localResult.exception)
            is Result.Canceled -> Result.Canceled(localResult.exception)
        }
    }
}