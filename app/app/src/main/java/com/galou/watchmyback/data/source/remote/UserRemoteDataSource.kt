package com.galou.watchmyback.data.source.remote

import android.net.Uri
import androidx.core.net.toUri
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.UserDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.USER_COLLECTION_NAME
import com.galou.watchmyback.utils.USER_PICTURE_REFERENCE
import com.galou.watchmyback.utils.await
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by galou on 2019-10-30
 */
class UserRemoteDataSource : UserDataSource{

    private val ioDispatcher = Dispatchers.IO

    private val remoteDB = FirebaseFirestore.getInstance()
    private val remoteStorage = FirebaseStorage.getInstance()
    private val userCollection = remoteDB.collection(USER_COLLECTION_NAME)

    override suspend fun createUser(user: User): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            userCollection.document(user.id).set(user).await()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateUserInformation(user: User): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            userCollection.document(user.id).update(
                "username", user.username,
                "email", user.email,
                "phoneNumber", user.phoneNumber,
                "pictureUrl", user.pictureUrl
            ).await()
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    suspend fun createNewPictureInStorageAndGetUri(userId: String, internalUri: Uri): Result<Uri> = withContext(ioDispatcher){
        return@withContext try {
            uploadPictureToRemoteStorage(internalUri, userId)
            fetchPictureUriInRemoteStorage(userId)
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    private fun getReferenceUserPictureStorage(userId: String) = remoteStorage.reference
        .child("$USER_PICTURE_REFERENCE$userId")

    private suspend fun uploadPictureToRemoteStorage(internalUrl: Uri, userId: String) = withContext(ioDispatcher){
        getReferenceUserPictureStorage(userId).putFile(internalUrl).await<UploadTask.TaskSnapshot>()
    }

    private suspend fun fetchPictureUriInRemoteStorage(userId: String): Result<Uri> = withContext(ioDispatcher){
        return@withContext try {
            getReferenceUserPictureStorage(userId).downloadUrl.await()
        } catch (e: Exception){
            Result.Error(e)
        }
    }


    suspend fun fetchUser(userId: String): Result<User?> = withContext(ioDispatcher) {
        return@withContext try {
            when (val resultDocumentSnapshot =
                userCollection.document(userId).get().await()) {
                is Result.Success -> {
                    val user = resultDocumentSnapshot.data.toObject(User::class.java)
                    Result.Success(user)
                }
                is Result.Error -> Result.Error(resultDocumentSnapshot.exception)
                is Result.Canceled -> Result.Canceled(resultDocumentSnapshot.exception)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteUser(user: User): Result<Void?> = withContext(ioDispatcher){
        return@withContext try {
            userCollection.document(user.id).delete().await()
        } catch (e: Exception){
            Result.Error(e)
        }
    }
}