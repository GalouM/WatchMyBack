package com.galou.watchmyback.data.source.local

import com.galou.watchmyback.data.entity.Friend
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.FriendDataSource
import com.galou.watchmyback.data.source.local.dao.FriendDao
import com.galou.watchmyback.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author galou
 * 2019-11-04
 */

class FriendLocalDataSource(
    private val friendDao: FriendDao
) : FriendDataSource {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun addFriend(friend: Friend): Result<Void?> = withContext(ioDispatcher){
        return@withContext try {
            friendDao.addFriend(friend)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }

    }

    override suspend fun removeFriend(userId: String, friendId: String): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            friendDao.removeFriend(userId, friendId)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun fetchUserFriend(userId: String): Result<List<User>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(friendDao.getFriendsUser(userId))

        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}