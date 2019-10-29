package com.galou.watchmyback.data.repository

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.USER_COLLECTION_NAME
import com.galou.watchmyback.utils.USER_PICTURE_REFERENCE
import com.galou.watchmyback.utils.await
import com.galou.watchmyback.utils.displayData
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Main Entry point to access the [User] data
 *
 * @see User
 *
 */

class UserRepositoryImpl : UserRepository {

    override val currentUser = MutableLiveData<User>()

    // REMOTE REQUEST

    private val remoteDB = FirebaseFirestore.getInstance()

    private val remoteStorage = FirebaseStorage.getInstance()

    private val userCollection = remoteDB.collection(USER_COLLECTION_NAME)


    override suspend fun getUserFromRemoteDB(userId: String): Result<User?> {
        return when(val resultDocumentSnapshot = userCollection.document(userId).get().await()){
            is Result.Success -> {
                val user = resultDocumentSnapshot.data.toObject(User::class.java)
                Result.Success(user)
            }
            is Result.Error -> Result.Error(resultDocumentSnapshot.exception)
            is Result.Canceled -> Result.Canceled(resultDocumentSnapshot.exception)
        }
    }


    override suspend fun createUserInRemoteDB(user: User) = userCollection.document(user.id).set(user).await()

    override suspend fun deleteUserFromCloudDB(userId: String) = userCollection.document(userId).delete().await()

    override suspend fun updateUserInfoInRemoteDB(user: User): Result<Void?> {
        return userCollection.document(user.id).update(
            "username", user.username,
            "email", user.email,
            "phoneNumber", user.phoneNumber
        ).await()
    }

    override suspend fun updateUserPicturePathInRemoteDB(
        userId: String,
        urlPicure: String
    ): Result<Void?> {
        return userCollection.document(userId).update(
            "pictureUrl", urlPicure
        ).await()
    }

    private fun getReferenceUserPictureStorage(userId: String) = remoteStorage.reference
        .child("${USER_PICTURE_REFERENCE}$userId")

    override suspend fun uploadUserPictureToRemoteStorageAndGetUrl(uriPicture: Uri): Result<Uri>{
        val referenceStorage = getReferenceUserPictureStorage(currentUser.value!!.id)
        referenceStorage.putFile(uriPicture).await<UploadTask.TaskSnapshot>()
        return referenceStorage.downloadUrl.await()

    }

}