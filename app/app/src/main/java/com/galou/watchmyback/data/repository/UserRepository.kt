package com.galou.watchmyback.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.data.entity.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.StorageReference
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
     * Get the specified [User] from the Cloud Firestore database
     *
     * @param userId ID of the user to query
     * @return [User] [Task] to observe to get the user information
     */
    fun getUserFromRemoteDB(userId: String): Task<User>

    /**
     * Create a [User] in the the Cloud Firestore database
     *
     * @param user [User] to create in the database
     * @return Creation [Task] to observe
     */
    fun createUserInRemoteDB(user: User): Task<Void>

    /**
     * Delete the specified [User] in the Cloud Firestore database
     *
     * @param userId ID of the [User] to delete
     * @return Delete [Task] to observe
     */
    fun deleteUserFromCloudDB(userId: String): Task<Void>

    /**
     * Update a [User] in the remote database
     *
     * @param user [User] to update
     * @return Update [Task] to observe
     */
    fun updateUserInRemoteDB(user: User): Task<Void>

    /**
     * Upload a [User]'s profile picture to Firebase Storage
     *
     * @param uriPicture local url of the picture to upload
     * @return [UploadTask] to observe
     */
    fun uploadUserPictureToRemoteStorageAndGetUrl(uriPicture: Uri): Task<Uri>
}