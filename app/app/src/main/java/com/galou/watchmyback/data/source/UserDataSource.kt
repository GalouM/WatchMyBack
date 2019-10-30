package com.galou.watchmyback.data.source

import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.Result

/**
 * Created by galou on 2019-10-30
 */
interface UserDataSource {

    suspend fun createUser(user: User): Result<Void?>

    suspend fun updateUserInformation(user: User): Result<Void?>

    suspend fun fetchUser(userId: String): Result<User?>

    suspend fun deleteUser(user: User): Result<Void?>


}