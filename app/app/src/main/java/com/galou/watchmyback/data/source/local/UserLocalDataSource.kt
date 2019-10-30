package com.galou.watchmyback.data.source.local

import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.source.UserDataSource
import com.galou.watchmyback.data.source.local.dao.UserDao
import com.galou.watchmyback.data.source.local.dao.UserPreferencesDao
import com.galou.watchmyback.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by galou on 2019-10-30
 */
class UserLocalDataSource(
    private val userDao: UserDao,
    private val userPreferencesDao: UserPreferencesDao
) : UserDataSource {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun createUser(user: User): Result<Void?>  = withContext(ioDispatcher){
        val preferences = UserPreferences(id = user.id)
        return@withContext try {
            userDao.createUserAndPreferences(user, preferences)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateUserInformation(user: User): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            userDao.updateUser(user)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun fetchUser(userId: String): Result<User?> = withContext(ioDispatcher){
        return@withContext try {
            Result.Success(userDao.getUser(userId))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteUser(user: User): Result<Void?> = withContext(ioDispatcher){
       return@withContext try {
           userDao.deleteUser(user)
           Result.Success(null)
       } catch (e: Exception) {
           Result.Error(e)
       }
    }

    suspend fun updateUserPreference(preferences: UserPreferences): Result<Void?> = withContext(ioDispatcher){
        return@withContext try {
            userPreferencesDao.updateUserPreferences(preferences)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}