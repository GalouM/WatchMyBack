package com.galou.watchmyback.data.source

import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.utils.Result

/**
 * Created by galou on 2019-10-30
 */
interface UserPreferencesSource {

    suspend fun createUserPreferences(userId: String, preferences: UserPreferences): Result<Void>

    suspend fun updateUserPreferences(userId: String, preferences: UserPreferences): Result<Void>

    suspend fun fetchUserPreferences(userId: String): Result<UserPreferences?>

    suspend fun deleteUserPreferences(userId: String): Result<Void>

}