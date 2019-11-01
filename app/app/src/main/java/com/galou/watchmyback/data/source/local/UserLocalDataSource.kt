package com.galou.watchmyback.data.source.local

import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.entity.UserWithPreferences
import com.galou.watchmyback.data.source.UserDataSource
import com.galou.watchmyback.data.source.local.dao.UserDao
import com.galou.watchmyback.data.source.local.dao.UserPreferencesDao
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.displayData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of [UserDataSource] for the local database
 *
 * List all the possible action on the local database for a [User]
 *
 *
 * @property userDao [UserDao]
 * @property userPreferencesDao [UserPreferencesDao]
 *
 * @see UserDataSource
 * @see UserDao
 * @see UserPreferencesDao
 * @see UserDataSource
 */
class UserLocalDataSource(
    private val userDao: UserDao,
    private val userPreferencesDao: UserPreferencesDao
) : UserDataSource {

    private val ioDispatcher = Dispatchers.IO

    /**
     * Create a [User] and his/her [UserPreferences] in the local database
     *
     * @param user [User] to create
     * @return [Result] of the creation
     *
     * @see UserDao.getUserWithPreferences
     */
    override suspend fun createUser(user: User): Result<Void?>  = withContext(ioDispatcher){
        val preferences = UserPreferences(id = user.id)
        return@withContext try {
            userDao.createUserAndPreferences(user, preferences)

            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Update or create the [User] in the local database
     *
     * Receive the User information from the remote database and the local database.
     * If the two user are different the local user is updated in the local database
     * If no local user exist with this ID the local user is created
     *
     * @param remoteUser [User] from the remote database
     * @param localUser [User] from the local database
     * @return [Result] containing a [User] and his/her [UserPreferences]
     *
     * @see updateUserInformation
     * @see fetchUser
     * @see createUser
     */
    suspend fun updateOrCreateUser(remoteUser: User, localUser: UserWithPreferences?): Result<UserWithPreferences?> {
        return  try {
            if (localUser != null) {
                if (remoteUser != localUser.user) updateUserInformation(remoteUser)
            } else {
                createUser(remoteUser)
            }
            val fetchUser = fetchUser(remoteUser.id)
            if (fetchUser is Result.Success){
                Result.Success(fetchUser.data)
            } else {
                Result.Error(Exception("Error fetching user"))
            }

        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Update the [User] information with the new data
     *
     * @param user [User] to update
     * @return [Result] of the operation
     *
     * @see UserDao.updateUser
     */
    override suspend fun updateUserInformation(user: User): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            userDao.updateUser(user)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Return a [User] and his/her [UserPreferences] if he/she exist in the local database
     *
     * @param userId ID of the [User] to look for
     * @return [UserWithPreferences] or null if no user exist
     *
     * @see UserDao.getUserWithPreferences
     */
    suspend fun fetchUser(userId: String): Result<UserWithPreferences?> = withContext(ioDispatcher){
        return@withContext try {
            Result.Success(userDao.getUserWithPreferences(userId))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    /**
     * Delete a [User] from the local database
     *
     * @param user [User] to delete
     * @return [Result] of the deletion
     *
     * @see UserDao.deleteUser
     */
    override suspend fun deleteUser(user: User): Result<Void?> = withContext(ioDispatcher){
       return@withContext try {
           userDao.deleteUser(user)
           Result.Success(null)
       } catch (e: Exception) {
           Result.Error(e)
       }
    }

    /**
     * Update the [UserPreferences] in the local database
     *
     * @param preferences [UserPreferences] to update
     * @return [Result] of the operation
     *
     * @see UserPreferencesDao.updateUserPreferences
     */
    suspend fun updateUserPreference(preferences: UserPreferences): Result<Void?> = withContext(ioDispatcher){
        return@withContext try {
            userPreferencesDao.updateUserPreferences(preferences)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}