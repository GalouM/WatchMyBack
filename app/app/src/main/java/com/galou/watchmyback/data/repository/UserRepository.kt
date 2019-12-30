package com.galou.watchmyback.data.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.entity.UserWithPreferences
import com.galou.watchmyback.utils.Result

/**
 * Interface to the [User] data layer
 *
 */
interface UserRepository {

    val currentUser: MutableLiveData<User>
    val userPreferences: MutableLiveData<UserPreferences>

    suspend fun createNewUser(user: User): Result<Void?>

    suspend fun updateUser(user: User): Result<Void?>

    suspend fun deleteUser(user: User): Result<Void?>

    suspend fun fetchUser(userId: String): Result<UserWithPreferences?>

    suspend fun updateUserPicture(user: User, internalUri: Uri): Result<Uri?>

    suspend fun updateUserPreferences(preferences: UserPreferences): Result<Void?>

    suspend fun fetchAllUsers(): Result<List<User>>

    suspend fun fetchUserByUsername(name: String): Result<List<User>>

    suspend fun fetchUserByEmailAddress(emailAddress: String): Result<List<User>>

    suspend fun fetchTripOwner(ownerId: String): Result<User>

    suspend fun fetchUserPreferences(userId: String): Result<UserPreferences?>

}