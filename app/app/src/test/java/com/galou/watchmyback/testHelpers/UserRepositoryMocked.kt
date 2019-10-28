package com.galou.watchmyback.testHelpers

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.UserRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask

/**
 * Created by galou on 2019-10-25
 */

open class UserRepositoryMocked : UserRepository{

    override val currentUser = MutableLiveData<User>()

    override fun getUserFromRemoteDB(userId: String): Task<User> =
        MockTask(
            generateTestUser(userId), true
        )

    override fun createUserInRemoteDB(user: User): Task<Void> =
        MockTask(null, true)

    override fun deleteUserFromCloudDB(userId: String): Task<Void> =
        MockTask(null, true)

    override fun updateUserInRemoteDB(user: User): Task<Void> =
        MockTask(null, true)

    override fun uploadUserPictureToRemoteStorageAndGetUrl(uriPicture: Uri): Task<Uri> {
        val uri = "http://myurl".toUri()
        return MockTask(uri, true)
    }
}