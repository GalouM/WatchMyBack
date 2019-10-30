package com.galou.watchmyback.data.repository

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.data.database.dao.UserDao
import com.galou.watchmyback.data.database.dao.UserPreferencesDao
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main Entry point to access the [User] data
 *
 * @see User
 *
 */

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val preferencesDao: UserPreferencesDao
) : UserRepository {

    override val currentUser = MutableLiveData<User>()

    private val ioDispatcher = Dispatchers.IO

    // LOCAL REQUEST

    suspend fun createUserInLocalDB(user: User, preferences: UserPreferences) =
        userDao.createUserAndPreferences(user, preferences)

    suspend fun updateUserInLocalDB(user: User) = userDao.updateUser(user)

    suspend fun updatePreferencesInLocalDB(preferences: UserPreferences) =
        preferencesDao.updateUserPreferences(preferences)

    suspend fun fetchUserPreferencesFomLocalDB(userId: String) =
        preferencesDao.getUserPreferences(userId)


    // REMOTE REQUEST

    private val remoteDB = FirebaseFirestore.getInstance()

    private val remoteStorage = FirebaseStorage.getInstance()

    private val userCollection = remoteDB.collection(USER_COLLECTION_NAME)


    override suspend fun getUserFromRemoteDB(userId: String): Result<User?> = withContext(ioDispatcher) {
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
        } catch (e: Exception){
            Result.Error(e)
        }
    }


    override suspend fun createUserInRemoteDB(user: User) = withContext(ioDispatcher){
        return@withContext try {
            userCollection.document(user.id).set(user).await()
        } catch (e: Exception){
            Result.Error(e)
        }
    }


    override suspend fun deleteUserFromCloudDB(userId: String) = withContext(ioDispatcher){
        return@withContext try {
            userCollection.document(userId).delete().await()
        } catch (e: Exception){
            Result.Error(e)
        }
    }


    override suspend fun updateUserInfoInRemoteDB(user: User): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            userCollection.document(user.id).update(
                "username", user.username,
                "email", user.email,
                "phoneNumber", user.phoneNumber
            ).await()
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    override suspend fun updateUserPicturePathInRemoteDB(
        userId: String,
        urlPicure: String
    ): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            userCollection.document(userId).update(
                "pictureUrl", urlPicure
            ).await()
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    private fun getReferenceUserPictureStorage(userId: String) = remoteStorage.reference
        .child("${USER_PICTURE_REFERENCE}$userId")

    override suspend fun uploadUserPictureToRemoteStorageAndGetUrl(uriPicture: Uri): Result<Uri> = withContext(ioDispatcher){
        val referenceStorage = getReferenceUserPictureStorage(currentUser.value!!.id)
        referenceStorage.putFile(uriPicture).await<UploadTask.TaskSnapshot>()
        return@withContext try{
            referenceStorage.downloadUrl.await()
        } catch (e: Exception){
            Result.Error(e)
        }

    }

}