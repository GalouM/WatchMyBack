package com.galou.watchmyback.data.repository

import androidx.core.net.toUri
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.USER_COLLECTION_NAME
import com.galou.watchmyback.utils.USER_PICTURE_REFERENCE
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Main Entry point to access the [User] data
 *
 * @see User
 *
 */

class UserRepositoryImpl : UserRepository {

    /**
     * [User] currently connected to the application
     */
    override var currentUser: User? = null


    // REMOTE REQUEST
    /**
     * Instance of the Cloud Firestore database
     */
    private val remoteDB = FirebaseFirestore.getInstance()
    /**
     * Instance of the Firebase Storage file storage
     */
    private val remoteStorage = FirebaseStorage.getInstance()
    /**
     * Instance to the [User] collection in Cloud Firestore database
     */
    private val userCollection = remoteDB.collection(USER_COLLECTION_NAME)

    /**
     * Get the specified [User] from the Cloud Firestore database
     *
     * @param userId ID of the user to query
     */
    override fun getUserFromRemoteDB(userId: String): Task<User>{
        val documentSnapshot = userCollection.document(userId).get()
        return documentSnapshot.continueWith {
            if (documentSnapshot.isSuccessful){
                return@continueWith documentSnapshot.result?.toObject(User::class.java)
            } else {
                return@continueWith null
            }
        }

    }

    /**
     * Create a [User] in the the Cloud Firestore database
     *
     * @param user [User] to create in the database
     */
    override fun createUserInRemoteDB(user: User) = userCollection.document(user.id).set(user)

    /**
     * Delete the specified [User] in the Cloud Firestore database
     *
     * @param userId ID of the [User] to delete
     */
    override fun deleteUserFromCloudDB(userId: String) = userCollection.document(userId).delete()

    /**
     * Get the reference of a [User] profile picture file from Firebase Storage
     *
     * @param userId ID of the user requested
     */
    private fun getReferenceUserPictureStorage(userId: String) = remoteStorage.reference
        .child("${USER_PICTURE_REFERENCE}$userId")

    /**
     * Upload a [User]'s profile picture to Firebase Storage
     *
     * @param urlPicture local url of the picture to upload
     * @param userId ID of the [User]
     */
    override fun uploadUserPicture(urlPicture: String, userId: String) = getReferenceUserPictureStorage(userId)
        .putFile(urlPicture.toUri())

}