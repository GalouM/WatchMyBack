package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.OtherUser
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.local.FriendLocalDataSource
import com.galou.watchmyback.data.source.remote.FriendRemoteDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.displayData
import com.galou.watchmyback.utils.extension.toListOtherUser
import com.galou.watchmyback.utils.returnSuccessOrError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Implementation of [FriendRepository]
 *
 * List all the possible actions ona Friend
 *
 * @property localSource Access to the local database
 * @property remoteSource Access to the remote database
 */
class FriendRepositoryImpl(
    private val localSource: FriendLocalDataSource,
    private val remoteSource: FriendRemoteDataSource
) : FriendRepository {

    /**
     * Run Async task to add a friend in the remote and local databases
     *
     * @param user [User] who own the friend
     * @param friend [User] added as a friend
     * @return [Result] of the creation of the friend
     *
     * @see FriendLocalDataSource.addFriend
     * @see FriendRemoteDataSource.removeFriend
     */
    override suspend fun addFriend(user: User, friend: User): Result<Void?> = coroutineScope {
        val localTask = async { localSource.addFriend(user, friend) }
        val remoteTask = async { remoteSource.addFriend(user, friend) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }

    /**
     * Run async task to remove a friend in the remote and local database
     *
     * @param user [User] who own the friend
     * @param friendId ID of the friend to remove
     * @return [Result] of the deletion
     *
     * @see FriendLocalDataSource.removeFriend
     * @see FriendRemoteDataSource.removeFriend
     */
    override suspend fun removeFriend(user: User, friendId: String): Result<Void?> = coroutineScope {
        val localTask = async { localSource.removeFriend(user, friendId) }
        val remoteTask = async { remoteSource.removeFriend(user, friendId) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }

    /**
     * Fetch all the friend of a [User]
     *
     * If refresh is true the friends will be fetched from the remote database and copied to the local one
     * Otherwise the friends will be fetched from the local databse
     *
     * @param user
     * @param refresh determine is the friends should be fetched from the remote or the local database
     * @return [Result] containing a list of [User]
     *
     * @see FriendLocalDataSource.fetchUserFriend
     * @see FriendLocalDataSource.addFriend
     * @see FriendRemoteDataSource.fetchUserFriend
     */
    override suspend fun fetchUserFriend(user: User, refresh: Boolean): Result<List<OtherUser>> {
        displayData("refresh = $refresh")
        when (refresh) {
            true -> {
                val remoteResult = remoteSource.fetchUserFriend(user)
                if (remoteResult is Result.Success) {
                    displayData("remote friends ${remoteResult.data}")
                    if (remoteResult.data.isNotEmpty()) localSource.addFriend(user, *remoteResult.data.toTypedArray())
                    return Result.Success(remoteResult.data.toListOtherUser(true))
                }
            }
        }
        return when (val localResult = localSource.fetchUserFriend(user)) {
            is Result.Success -> {
                displayData("local friends ${localResult.data}")
                Result.Success(localResult.data.toListOtherUser(true))
            }
            is Result.Error -> Result.Error(localResult.exception)
            is Result.Canceled -> Result.Canceled(localResult.exception)
        }
    }
}