package com.galou.watchmyback.data.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.entity.UserWithPreferences
import com.galou.watchmyback.data.source.local.UserLocalDataSource
import com.galou.watchmyback.data.source.remote.UserRemoteDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.displayData
import com.galou.watchmyback.utils.returnSuccessOrError
import kotlinx.coroutines.*
import java.lang.Error

/**
 * Main Entry point to access the [User] data
 *
 * @see User
 *
 */

class UserRepositoryImpl(
    private val localSource: UserLocalDataSource,
    private val remoteSource: UserRemoteDataSource
) : UserRepository {

    override val currentUser = MutableLiveData<User>()
    override val userPreferences = MutableLiveData<UserPreferences>()

    private val ioDispatcher = Dispatchers.IO


    override suspend fun createUser(user: User): Result<Void?> = withContext(ioDispatcher) {
        val localTask = async { localSource.createUser(user) }
        val remoteTask = async { remoteSource.createUser(user) }

        return@withContext returnSuccessOrError(localTask.await(), remoteTask.await())


    }

    override suspend fun updateUser(user: User): Result<Void?> = withContext(ioDispatcher) {
        val localTask = async { localSource.updateUserInformation(user) }
        val remoteTask = async { remoteSource.updateUserInformation(user) }

        return@withContext returnSuccessOrError(localTask.await(), remoteTask.await())

    }

    override suspend fun deleteUser(user: User): Result<Void?> = withContext(ioDispatcher) {
        val localTask = async { localSource.deleteUser(user) }
        val remoteTask = async { remoteSource.deleteUser(user) }

        return@withContext returnSuccessOrError(localTask.await(), remoteTask.await())
    }

    override suspend fun fetchUser(userId: String): Result<UserWithPreferences?> = withContext(ioDispatcher) {
        val localTask =  async { localSource.fetchUser(userId) }
        val remoteTask = async { remoteSource.fetchUser(userId) }
        val remoteResult = remoteTask.await()
        val localResult = localTask.await()
        if (remoteResult is Result.Success && localResult is Result.Success){
            remoteResult.data?.let {remoteUser ->
                val localUser = localResult.data
                localSource.updateOrCreateUser(remoteUser, localUser)
                val userWithPreferences = UserWithPreferences(user = remoteUser, preferences = localUser?.preferences)
                return@withContext Result.Success(userWithPreferences)
            }

        }

        return@withContext localResult
    }

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

    override suspend fun updateUserPreferences(preferences: UserPreferences): Result<Void?> =
        localSource.updateUserPreference(preferences)

}