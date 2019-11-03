package com.galou.watchmyback.data.source

import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.Result

/**
 * Main Entry point for accessing the [User] data
 *
 */
interface UserDataSource {

    suspend fun createUser(user: User): Result<Void?>

    suspend fun updateUserInformation(user: User): Result<Void?>

    suspend fun deleteUser(user: User): Result<Void?>


}