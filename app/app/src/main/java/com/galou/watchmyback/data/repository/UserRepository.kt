package com.galou.watchmyback.data.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.Result
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.UploadTask

/**
 * Created by galou on 2019-10-25
 */

interface UserRepository {
    /**
     * [User] currently connected to the application
     * @see User
     */
    val currentUser: MutableLiveData<User>
    /**
     * Suspend function, get the specified [User] from the Cloud Firestore database
     *
     * @param userId ID of the user to query
     * @return [User] requested if it exist in the database
     */
    suspend fun getUserFromRemoteDB(userId: String): Result<User>

    /**
     * Suspend function, create a [User] in the the Cloud Firestore database
     *
     * @param user [User] to create in the database
     * @return [Void] result of the creation, if ti was successful or not
     */
    suspend fun createUserInRemoteDB(user: User): Result<Void?>

    /**
     * Delete the specified [User] in the Cloud Firestore database
     *
     * @param userId ID of the [User] to delete
     * @return [Void] or null if the task was successful or not
     */
    suspend fun deleteUserFromCloudDB(userId: String): Result<Void?>

    /**
     * Update a [User] in the remote database
     *
     * @param user [User] to update
     * @return [Void] or null if the task was successful or not
     */
    suspend fun updateUserInRemoteDB(user: User): Result<Void?>

    /**
     * Upload a [User]'s profile picture to Firebase Storage
     *
     * @param uriPicture local url of the picture to upload
     * @return [UploadTask] to observe
     */
    suspend fun uploadUserPictureToRemoteStorageAndGetUrl(uriPicture: Uri): Result<Uri>
}