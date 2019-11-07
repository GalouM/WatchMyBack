package com.galou.watchmyback.data.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.entity.UserWithPreferences
import com.galou.watchmyback.data.source.local.FriendLocalDataSource
import com.galou.watchmyback.data.source.local.UserLocalDataSource
import com.galou.watchmyback.data.source.remote.FriendRemoteDataSource
import com.galou.watchmyback.data.source.remote.UserRemoteDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.returnSuccessOrError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Implementation of [UserRepository]
 *
 * List all the possible actions on a [User]
 *
 * @property userLocalSource Access to the local database
 * @property userRemoteSource Access to the remote database
 *
 * @see User
 *
 */

class UserRepositoryImpl(
    private val userLocalSource: UserLocalDataSource,
    private val userRemoteSource: UserRemoteDataSource,
    private val friendRemoteSource: FriendRemoteDataSource
) : UserRepository {

    /**
     * Current user connected to the application
     */
    override val currentUser = MutableLiveData<User>()
    /**
     * preferences of the current user
     */
    override val userPreferences = MutableLiveData<UserPreferences>()

    /**
     * Run async task to create a new [User] on the local and remote database
     *
     * @param user [User] to create
     * @return [Result] of the creation
     *
     * @see UserLocalDataSource.createUser
     * @see UserRemoteDataSource.createUser
     */
    override suspend fun createNewUser(user: User): Result<Void?> = coroutineScope {
        val localTask = async { userLocalSource.createUser(user) }
        val remoteTask = async { userRemoteSource.createUser(user) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }

    /**
     * Run async task to update a [User] on the local and remote database
     *
     * @param user [User] to update
     * @return [Result] of the operation
     *
     * @see UserLocalDataSource.updateUserInformation
     * @see UserRemoteDataSource.updateUserInformation
     */
    override suspend fun updateUser(user: User): Result<Void?> = coroutineScope {
        val localTask = async { userLocalSource.updateUserInformation(user) }
        val remoteTask = async { userRemoteSource.updateUserInformation(user) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }

    /**
     * Run async task to delete the [User] on the local and remote database
     *
     * @param user [User] to delete
     * @return [Result] of the operation
     *
     * @see UserLocalDataSource.deleteUser
     * @see UserRemoteDataSource.deleteUser
     */
    override suspend fun deleteUser(user: User): Result<Void?> = coroutineScope {
        val localTask = async { userLocalSource.deleteUser(user) }
        val remoteTask = async { userRemoteSource.deleteUser(user) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }

    /**
     * Run async task to fetch a user's data from the database
     *
     * If the remote database is not available it will return the [User]'s data from the local database
     * If the [User]'s data exist in the remote database the user's data from the local database
     * will be updated or created accordingly
     *
     * @param userId ID of the [User] to fetch
     * @return a [User]'s data with his/her [UserPreferences]
     *
     * @see UserLocalDataSource.fetchUser
     * @see UserRemoteDataSource.fetchUser
     * @see UserLocalDataSource.updateOrCreateUser
     */
    override suspend fun fetchUser(userId: String): Result<UserWithPreferences?> = coroutineScope {
        // fetch user from both database
        val localUserTask =  async { userLocalSource.fetchUser(userId) }
        val remoteUserTask = async { userRemoteSource.fetchUser(userId) }
        // wait for result DB and make sure fetch succeed
        val remoteResult = remoteUserTask.await()
        val localResult = localUserTask.await()

        when {
            remoteResult is Result.Success && localResult is Result.Success -> {
                remoteResult.data?.let {remoteUser ->
                    // fetch user's friends
                    // make sure friends fetch succeed and update or create the user with it data
                    when (val remoteFriendTask = friendRemoteSource.fetchUserFriend(remoteUser)) {
                        is Result.Success -> {
                            when(val updateLocalDbResult =
                                userLocalSource.updateOrCreateUser(remoteUser, localResult.data,
                                    *remoteFriendTask.data.toTypedArray())) {
                                is Result.Success -> return@coroutineScope Result.Success(updateLocalDbResult.data)

                                else -> return@coroutineScope localResult
                            }

                        }
                        else -> return@coroutineScope localResult
                    }
                }
            }

        }

        return@coroutineScope localResult
    }

    /**
     * Update the profile picture information of a [User]
     *
     * @param user [User]'s data to update
     * @param internalUri local [Uri] of the picture
     * @return [Result] that contains the [Uri] of the picture in the remote storage
     *
     * @see UserRemoteDataSource.createNewPictureInStorageAndGetUri
     */
    override suspend fun updateUserPicture(user: User, internalUri: Uri): Result<Uri?> {
        val uriResult = userRemoteSource.createNewPictureInStorageAndGetUri(user.id, internalUri)

        when(uriResult){
            is Result.Success -> {
                val uriRemotePicture = uriResult.data.toString()
                user.pictureUrl = uriRemotePicture
                when (updateUser(user)) {
                    is Result.Error, is Result.Canceled -> {
                        return Result.Error(Exception("Error while updating user information"))
                    }
                }
            }
        }
        return uriResult
    }

    /**
     * Update the [UserPreferences]'s data of a [User]
     *
     * @param preferences [UserPreferences] to update
     * @return [Result] of the operation
     *
     * @see UserLocalDataSource.updateUserInformation
     */
    override suspend fun updateUserPreferences(preferences: UserPreferences): Result<Void?> =
        userLocalSource.updateUserPreference(preferences)

    /**
     * Run async task to fetch all the user from the database
     *
     * By default it will fetch the users from the remote database
     * If the remote database is not available it will fetch the user from the local database
     *
     * @return [Result] with a list of [User]
     *
     * @see UserRemoteDataSource.fetchAllUsers
     * @see UserLocalDataSource.fetchAllUsers
     */
    override suspend fun fetchAllUsers(): Result<List<User>> {
        when(val remoteResult = userRemoteSource.fetchAllUsers()){
            is Result.Success -> if (remoteResult.data.isNotEmpty()) return remoteResult
        }
        return userLocalSource.fetchAllUsers()
    }

    /**
     * Run async task to fetch all the user from the database who have a specific chain of character in their username
     *
     * By default it will fetch the users from the remote database
     * If the remote database is not available it will fetch the user from the local database
     *
     * @param name string to look for in the username
     * @return [Result] with a list of [User]
     *
     * @see UserRemoteDataSource.fetchUserByUsername
     * @see UserLocalDataSource.fetchUserByUsername
     */
    override suspend fun fetchUserByUsername(name: String): Result<List<User>> {
        when(val remoteResult = userRemoteSource.fetchUserByUsername(name)){
            is Result.Success -> if (remoteResult.data.isNotEmpty()) return remoteResult
        }
        return userLocalSource.fetchUserByUsername(name)
    }

    /**
     * Run async task to fetch all the user from the database who have a specific chain of character in their email address
     *
     * By default it will fetch the users from the remote database
     * If the remote database is not available it will fetch the user from the local database
     *
     * @param emailAddress string to look for in the address
     * @return [Result] with a list of [User]
     *
     * @see UserRemoteDataSource.fetchUserByEmailAddress
     * @see UserLocalDataSource.fetchUserByEmailAddress
     */
    override suspend fun fetchUserByEmailAddress(emailAddress: String): Result<List<User>> {
        when(val remoteResult = userRemoteSource.fetchUserByEmailAddress(emailAddress)){
            is Result.Success -> if (remoteResult.data.isNotEmpty()) return remoteResult
        }
        return userLocalSource.fetchUserByEmailAddress(emailAddress)
    }
}