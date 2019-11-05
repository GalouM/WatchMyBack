package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.Friend
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.local.FriendLocalDataSource
import com.galou.watchmyback.data.source.remote.FriendRemoteDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.returnSuccessOrError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

/**
 * @author galou
 * 2019-11-04
 */
class FriendRepositoryImpl(
    private val localSource: FriendLocalDataSource,
    private val remoteSource: FriendRemoteDataSource
) : FriendRepository {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun addFriend(userId: String, friendId: String): Result<Void?> = withContext(ioDispatcher) {
        val friend = Friend(userId, friendId)
        val localTask = async { localSource.addFriend(friend) }
        val remoteTask = async { remoteSource.addFriend(friend) }

        return@withContext returnSuccessOrError(localTask.await(), remoteTask.await())

    }

    override suspend fun removeFriend(userId: String, friendId: String): Result<Void?> = withContext(ioDispatcher) {
        val localTask = async { localSource.removeFriend(userId, friendId) }
        val remoteTask = async { remoteSource.removeFriend(userId, friendId) }

        return@withContext returnSuccessOrError(localTask.await(), remoteTask.await())

    }

    override suspend fun fetchUserFriend(userId: String): Result<List<User>> {
        val remoteResult = remoteSource.fetchUserFriend(userId)
        if (remoteResult is Result.Success){
            if (remoteResult.data.isNotEmpty()){
                return remoteResult
            }
        }
        return localSource.fetchUserFriend(userId)
    }
}