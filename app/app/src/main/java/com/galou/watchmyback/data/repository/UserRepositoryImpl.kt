package com.galou.watchmyback.data.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.entity.UserWithPreferences
import com.galou.watchmyback.data.source.local.UserLocalDataSource
import com.galou.watchmyback.data.source.remote.UserRemoteDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.returnSuccessOrError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

/**
 * Implementation of [UserRepository]
 *
 * List all the possible actions on a [User]
 *
 * @property localSource Access to the local database
 * @property remoteSource Access to the remote database
 *
 * @see User
 *
 */

class UserRepositoryImpl(
    private val localSource: UserLocalDataSource,
    private val remoteSource: UserRemoteDataSource
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
     * dispatcher to run coroutine on
     */
    private val ioDispatcher = Dispatchers.IO


    /**
     * Run async task to create a [User] on the local and remote database
     *
     * @param user [User] to create
     * @return [Result] of the creation
     *
     * @see UserLocalDataSource.createUser
     * @see UserRemoteDataSource.createUser
     */
    override suspend fun createUser(user: User): Result<Void?> = withContext(ioDispatcher) {
        val localTask = async { localSource.createUser(user) }
        val remoteTask = async { remoteSource.createUser(user) }

        return@withContext returnSuccessOrError(localTask.await(), remoteTask.await())


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
    override suspend fun updateUser(user: User): Result<Void?> = withContext(ioDispatcher) {
        val localTask = async { localSource.updateUserInformation(user) }
        val remoteTask = async { remoteSource.updateUserInformation(user) }

        return@withContext returnSuccessOrError(localTask.await(), remoteTask.await())

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
    override suspend fun deleteUser(user: User): Result<Void?> = withContext(ioDispatcher) {
        val localTask = async { localSource.deleteUser(user) }
        val remoteTask = async { remoteSource.deleteUser(user) }

        return@withContext returnSuccessOrError(localTask.await(), remoteTask.await())
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
    override suspend fun fetchUser(userId: String): Result<UserWithPreferences?> = withContext(ioDispatcher) {
        val localTask =  async { localSource.fetchUser(userId) }
        val remoteTask = async { remoteSource.fetchUser(userId) }

        val remoteResult = remoteTask.await()
        val localResult = localTask.await()

        if (remoteResult is Result.Success && localResult is Result.Success){
            remoteResult.data?.let {remoteUser ->
                val updateLocalDbResult = localSource.updateOrCreateUser(remoteUser, localResult.data)
                if (updateLocalDbResult is Result.Success){
                    return@withContext Result.Success(updateLocalDbResult.data)
                } else {
                    return@withContext updateLocalDbResult
                }
            }

        }

        return@withContext localResult
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
        val uriResult = remoteSource.createNewPictureInStorageAndGetUri(user.id, internalUri)
        if(uriResult is Result.Success){
            val uriRemotePicture = uriResult.data.toString()
            user.pictureUrl = uriRemotePicture
            val updateUserResult = updateUser(user)
            if(updateUserResult is Result.Error || updateUserResult is Result.Canceled ){
                return Result.Error(Exception("Error while updating user information"))
            }
        }
        return uriResult
    }

    /**
     * Update the [UserPreferences]'s data of a [User]
     *
     * @param preferences [UserPreferences] to update
     * @return [Result] of the operation
     */
    override suspend fun updateUserPreferences(preferences: UserPreferences): Result<Void?> =
        localSource.updateUserPreference(preferences)

}