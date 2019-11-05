package com.galou.watchmyback.data.source.local

import com.galou.watchmyback.data.entity.Friend
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.FriendDataSource
import com.galou.watchmyback.data.source.local.dao.FriendDao
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-11-04
 */

class FriendLocalDataSource(
    private val friendDao: FriendDao
) : FriendDataSource {

    override suspend fun addFriend(friend: Friend): Result<Void?> {
        return try {
            friendDao.addFriend(friend)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }

    }

    override suspend fun removeFriend(userId: String, friendId: String): Result<Void?>  {
        return try {
            friendDao.removeFriend(userId, friendId)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun fetchUserFriend(userId: String): Result<List<User>>  {
        return try {
            Result.Success(friendDao.getFriendsUser(userId))

        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}