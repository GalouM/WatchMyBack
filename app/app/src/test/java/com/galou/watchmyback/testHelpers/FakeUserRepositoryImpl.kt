package com.galou.watchmyback.testHelpers

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.UserRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.galou.watchmyback.utils.Result

/**
 * Created by galou on 2019-10-25
 */

open class FakeUserRepositoryImpl : UserRepository{

    override val currentUser = MutableLiveData<User>()

    override suspend fun getUserFromRemoteDB(userId: String): Result<User> = Result.Success(generateTestUser(userId))

    override suspend fun createUserInRemoteDB(user: User): Result<Void?> = Result.Success(null)

    override suspend fun deleteUserFromCloudDB(userId: String): Result<Void?> = Result.Success(null)

    override suspend fun updateUserInRemoteDB(user: User): Result<Void?> = Result.Success(null)

    override suspend fun uploadUserPictureToRemoteStorageAndGetUrl(uriPicture: Uri): Result<Uri> {
        val uriFromRemoteStorage = URI_STORAGE_REMOTE.toUri()
        return Result.Success(uriFromRemoteStorage)

    }
}