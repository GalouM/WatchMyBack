package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

/**
 * Created by galou on 2019-10-25
 */

interface UserRepository {
    var currentUser: User?
    fun getUserFromRemoteDB(userId: String): Task<User>
    fun createUserInRemoteDB(user: User): Task<Void>
    fun deleteUserFromCloudDB(userId: String): Task<Void>
    fun updateUserInRemoteDB(user: User): Task<Void>
    fun uploadUserPicture(urlPicture: String, userId: String): UploadTask?
}