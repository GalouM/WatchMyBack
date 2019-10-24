package com.galou.watchmyback.data.repository

import androidx.core.net.toUri
import com.galou.watchmyback.data.database.dao.UserDao
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.USER_COLLECTION_NAME
import com.galou.watchmyback.utils.USER_PICTURE_REFERENCE
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Main Entry point to access the [User] data
 *
 * @see User
 *
 */

class UserRepository {

    /**
     * [User] currently connected to the application
     */
    var currentUser: User? = null


    // REMOTE REQUEST
    /**
     * Instance of the Cloud Firestore database
     */
    private val instanceRemoteDB = FirebaseFirestore.getInstance()
    /**
     * Instance of the Firebase Storage file storage
     */
    private val storageInstance = FirebaseStorage.getInstance()
    /**
     * Instance to the [User] collection in Cloud Firestore database
     */
    private val userCollection = instanceRemoteDB.collection(USER_COLLECTION_NAME)

    /**
     * Get the specified [User] from the Cloud Firestore database
     *
     * @param userId ID of the user to query
     */
    fun getUserFromRemoteDB(userId: String) = userCollection.document(userId).get()

    /**
     * Create a [User] in the the Cloud Firestore database
     *
     * @param user [User] to create in the database
     */
    fun createUserInRemoteDB(user: User) = userCollection.document(user.id).set(user)

    /**
     * Delete the specified [User] in the Cloud Firestore database
     *
     * @param userId ID of the [User] to delete
     */
    fun deleteUserFromCloudDB(userId: String) = userCollection.document(userId).delete()

    /**
     * Get the reference of a [User] profile picture file from Firebase Storage
     *
     * @param userId ID of the user requested
     */
    fun getReferenceUserPictureStorage(userId: String) = storageInstance.reference
        .child("${USER_PICTURE_REFERENCE}$userId")

    /**
     * Upload a [User]'s profile picture to Firebase Storage
     *
     * @param urlPicture local url of the picture to upload
     * @param userId ID of the [User]
     */
    fun uploadUserPicture(urlPicture: String, userId: String) = getReferenceUserPictureStorage(userId)
        .putFile(urlPicture.toUri())

}