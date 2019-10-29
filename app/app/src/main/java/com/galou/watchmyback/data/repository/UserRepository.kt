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
     * Suspend function, get the specified [User] from the Cloud Firestore database.
     *
     * Return a [Result] with a [User] or an error
     *
     * @param userId ID of the user to query
     * @return [Result] with the [User] requested if it exist in the database
     *
     * @see Result
     */
    suspend fun getUserFromRemoteDB(userId: String): Result<User>

    /**
     * Suspend function, create a [User] in the the Cloud Firestore database
     *
     * @param user [User] to create in the database
     * @return [Result] of the operation
     *
     * @see Result
     */
    suspend fun createUserInRemoteDB(user: User): Result<Void?>

    /**
     * Delete the specified [User] in the Cloud Firestore database
     *
     * @param userId ID of the [User] to delete
     * @return [Result] of the operation
     */
    suspend fun deleteUserFromCloudDB(userId: String): Result<Void?>

    /**
     * Update a [User] in the remote database
     *
     * @param user [User] to update
     * @return [Result] of the operation
     *
     * @see Result
     */
    suspend fun updateUserInRemoteDB(user: User): Result<Void?>

    /**
     * Upload a [User]'s profile picture to Firebase Storage
     *
     * The suspend function return a [Result] with the uri of the picture in the remote storage
     *
     * @param uriPicture local url of the picture to upload
     * @return [Result] with the uri of picture in the remote storage or error message
     *
     * @see Result
     */
    suspend fun uploadUserPictureToRemoteStorageAndGetUrl(uriPicture: Uri): Result<Uri>
}