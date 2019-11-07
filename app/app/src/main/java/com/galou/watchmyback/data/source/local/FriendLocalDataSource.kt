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

    override suspend fun addFriend(user: User, vararg friend: User): Result<Void?> {
        return try {
            friendDao.addFriend(user.id, *friend)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }

    }

    override suspend fun removeFriend(user: User, friendId: String): Result<Void?>  {
        return try {
            friendDao.removeFriend(user.id, friendId)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun fetchUserFriend(user: User): Result<List<User>>  {
        return try {
            Result.Success(friendDao.getFriendsUser(user.id))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}