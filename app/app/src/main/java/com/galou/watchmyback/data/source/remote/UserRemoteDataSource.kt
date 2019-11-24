package com.galou.watchmyback.data.source.remote

import android.net.Uri
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.UserDataSource
import com.galou.watchmyback.utils.*
import com.galou.watchmyback.utils.extension.toUser
import com.galou.watchmyback.utils.extension.toUserList
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of [UserDataSource] for the remote database
 *
 * List all the possible action on the remote database for a [User]
 *
 * @param remoteDB Reference toward the remote database
 * @param remoteStorage Reference toward the remote storage
 *
 */
class UserRemoteDataSource(
    remoteDB: FirebaseFirestore,
    private val remoteStorage: FirebaseStorage
) : UserDataSource{

    private val ioDispatcher = Dispatchers.IO
    private val userCollection = remoteDB.collection(USER_COLLECTION_NAME)

    /**
     * Create a [User] in the remote database
     *
     * @param user [User] to create
     * @return [Result] of the operation
     */
    override suspend fun createUser(user: User): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            userCollection.document(user.id).set(user).await()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Update the [User]'s information in the remote database
     *
     * @param user [User] to update
     * @return [Result] of the operation
     */
    override suspend fun updateUserInformation(user: User): Result<Void?> = withContext(ioDispatcher) {
        return@withContext userCollection.document(user.id).update(
            "username", user.username,
            "email", user.email,
            "phoneNumber", user.phoneNumber,
            "pictureUrl", user.pictureUrl
        ).await()
    }

    /**
     * Create the a new picture file in the remote storage and return its [Uri]
     *
     * @param userId ID of the [User] to whom belongs the profile picture
     * @param internalUri Local [Uri] of the picture
     * @return [Result] that contains the remote [Uri] of the picture
     *
     * @see uploadPictureToRemoteStorage
     * @see fetchPictureUriInRemoteStorage
     */
    suspend fun createNewPictureInStorageAndGetUri(userId: String, internalUri: Uri): Result<Uri> {
        return try {
            uploadPictureToRemoteStorage(internalUri, userId)
            fetchPictureUriInRemoteStorage(userId)
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    /**
     * Return the reference of a picture on the remote storage
     *
     * @param userId
     */
    private fun getReferenceUserPictureStorage(userId: String) = remoteStorage.reference
        .child("$USER_PICTURE_REFERENCE$userId")

    /**
     * Upload a picture to the remote storage
     *
     * @param internalUrl local [Uri] of the picture
     * @param userId ID of the [User] to whom belongs the picture
     *
     * @return [Result] of the uploading
     *
     * @see getReferenceUserPictureStorage
     */
    private suspend fun uploadPictureToRemoteStorage(internalUrl: Uri, userId: String) = withContext(ioDispatcher){
        getReferenceUserPictureStorage(userId).putFile(internalUrl).await<UploadTask.TaskSnapshot>()
    }

    /**
     * Fetch the [Uri] of a profile picture on the remote storage
     *
     * @param userId ID of the [User] to whom belongs the picture
     * @return [Result] that contains the [Uri] of the remote file
     *
     * @see getReferenceUserPictureStorage
     */
    private suspend fun fetchPictureUriInRemoteStorage(userId: String): Result<Uri> = withContext(ioDispatcher){
        return@withContext getReferenceUserPictureStorage(userId).downloadUrl.await()
    }


    /**
     * Fetch the [User] information from the remote database
     *
     * @param userId ID of the user
     * @return [Result] that contains the [User]'s data
     */
    suspend fun fetchUser(userId: String): Result<User?> = withContext(ioDispatcher) {
        return@withContext try {
            when (val resultDocumentSnapshot =
                userCollection.document(userId).get().await()) {
                is Result.Success -> Result.Success(resultDocumentSnapshot.data.toUser())
                is Result.Error -> Result.Error(resultDocumentSnapshot.exception)
                is Result.Canceled -> Result.Canceled(resultDocumentSnapshot.exception)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Delete the [User]'s data from the remote database
     *
     * @param user [User] to delete
     * @return [Result] of the deletion
     */
    override suspend fun deleteUser(user: User): Result<Void?> = withContext(ioDispatcher){
        return@withContext userCollection.document(user.id).delete().await()
    }

    /**
     * Fetch all the [User] from the remote database
     *
     * @return [Result] with a list of user
     */
    override suspend fun fetchAllUsers(): Result<List<User>> = withContext(ioDispatcher) {
        return@withContext try {
            when(val userQuery = userCollection.orderBy("username").get().await()){
                is Result.Success -> Result.Success(userQuery.data.toUserList())
                is Result.Error -> Result.Error(userQuery.exception)
                is Result.Canceled -> Result.Canceled(userQuery.exception)
            }
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    /**
     * Fetch all the user from the remote database who have a specific chain of character in their username
     *
     * @param name string to look for in the username
     * @return [Result] with a list of user
     */
    override suspend fun fetchUserByUsername(name: String): Result<List<User>> = withContext(ioDispatcher) {
        return@withContext try {
            when(val userQuery = userCollection
                .whereGreaterThanOrEqualTo("username", name)
                .whereLessThanOrEqualTo("username", name+ '\uf8ff')
                .orderBy("username")
                .get().await()){
                is Result.Success -> Result.Success(userQuery.data.toUserList())
                is Result.Error -> Result.Error(userQuery.exception)
                is Result.Canceled -> Result.Canceled(userQuery.exception)
            }
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    /**
     * Fetch all the user from the remote database who have a specific chain of character in their email address
     *
     * @param emailAddress string to look for in the email address
     * @return [Result] with a list of user
     */
    override suspend fun fetchUserByEmailAddress(emailAddress: String): Result<List<User>> = withContext(ioDispatcher) {
        return@withContext try {
            when(val userQuery = userCollection
                .whereGreaterThanOrEqualTo("email", emailAddress)
                .whereLessThanOrEqualTo("email", emailAddress+ '\uf8ff')
                .orderBy("username")
                .get().await()){
                is Result.Success -> Result.Success(userQuery.data.toUserList())
                is Result.Error -> Result.Error(userQuery.exception)
                is Result.Canceled -> Result.Canceled(userQuery.exception)
            }
        } catch (e: Exception){
            Result.Error(e)
        }
    }
}