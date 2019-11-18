package com.galou.watchmyback.data.source.local

import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.FriendDataSource
import com.galou.watchmyback.data.source.local.dao.FriendDao
import com.galou.watchmyback.utils.Result

/**
 * Implementation of [FriendDataSource] for the local database
 *
 * List all the possible actions on the local database for the friends
 *
 * @property friendDao [FriendDao]
 */
class FriendLocalDataSource(
    private val friendDao: FriendDao
) : FriendDataSource {

    /**
     * Add a friend to the user in the database
     *
     * @param user [User] who iwn the friend
     * @param friend [User] who is the friend
     * @return [Result] of the action
     *
     * @see FriendDao.addFriend
     */
    override suspend fun addFriend(user: User, vararg friend: User): Result<Void?> {
        return try {
            friendDao.addFriend(user.id, *friend)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }

    }

    /**
     * Remove a User's friend in the database
     *
     * @param user [User] user who iwn the friend
     * @param friendId id of the user who is the friend
     * @return [Result] of the deletion
     *
     * @see FriendDao.removeFriend
     */
    override suspend fun removeFriend(user: User, friendId: String): Result<Void?>  {
        return try {
            friendDao.removeFriend(user.id, friendId)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Fetch all the friend of a [User] from the database
     *
     * @param user Look for the friends of this [User]
     * @return [Result] containing a list of [User]
     *
     * @see FriendDao.getFriendsUser
     */
    override suspend fun fetchUserFriend(user: User): Result<List<User>>  {
        return try {
            Result.Success(friendDao.getFriendsUser(user.id))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}