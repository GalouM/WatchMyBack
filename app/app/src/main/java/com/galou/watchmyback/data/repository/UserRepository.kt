package com.galou.watchmyback.data.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.source.UserDataSource
import com.galou.watchmyback.data.source.local.UserLocalDataSource
import com.galou.watchmyback.data.source.remote.UserRemoteDataSource
import com.galou.watchmyback.utils.Result
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by galou on 2019-10-25
 */

interface UserRepository {
    /**
     * [User] currently connected to the application
     * @see User
     */
    val currentUser: MutableLiveData<User>

    suspend fun createUser(user: User): Result<Void?>

    suspend fun updateUser(user: User): Result<Void?>

    suspend fun deleteUser(user: User): Result<Void?>

    suspend fun fetchUser(userId: String): Result<User?>

    suspend fun updateUserPicture(user: User, internalUri: Uri): Result<Uri?>
}