package com.galou.watchmyback.data.source.local

import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.entity.UserWithPreferences
import com.galou.watchmyback.data.source.UserDataSource
import com.galou.watchmyback.data.source.local.dao.UserDao
import com.galou.watchmyback.data.source.local.dao.UserPreferencesDao
import com.galou.watchmyback.utils.Result

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
 */
class UserLocalDataSource(
    private val userDao: UserDao,
    private val userPreferencesDao: UserPreferencesDao
) : UserDataSource {

    /**
     * Create a [User] and his/her [UserPreferences] in the local database
     *
     * @param user [User] to create
     * @return [Result] of the creation
     *
     * @see UserDao.getUserWithPreferences
     */
    override suspend fun createUser(user: User): Result<Void?>  {
        val preferences = UserPreferences(id = user.id)
        return try {
            userDao.createOrUpdateUserWithData(true , user, preferences)
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
        val createUser = localUser == null
        val defaultPreferences = UserPreferences(id = remoteUser.id)
        return try {
            userDao.createOrUpdateUserWithData(createUser, remoteUser, defaultPreferences)
            val preferences = userPreferencesDao.getUserPreferences(remoteUser.id)
            Result.Success(UserWithPreferences(
            user = remoteUser,
            preferences = preferences ?: defaultPreferences
            ))
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
    override suspend fun updateUserInformation(user: User): Result<Void?>  {
        return try {
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
    suspend fun fetchUser(userId: String): Result<UserWithPreferences?> {
        return try {
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
    override suspend fun deleteUser(user: User): Result<Void?> {
       return try {
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
    suspend fun updateUserPreference(preferences: UserPreferences): Result<Void?> {
        return try {
            userPreferencesDao.updateUserPreferences(preferences)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Fetch all the [User] from the local database
     *
     * @return a [Result] with a list of user
     */
    override suspend fun fetchAllUsers(): Result<List<User>> {
        return try {
            Result.Success(userDao.getAllUsers())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Fetch all the user from the local database who have a specific chain of character in their username
     *
     * @param name string to look for in the username
     * @return [Result] with a list of user
     */
    override suspend fun fetchUserByUsername(name: String): Result<List<User>>  {
        return try {
            Result.Success(userDao.getUsersFromUsername(name))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Fetch all the user from the local database who have a specific chain of character in their email address
     *
     * @param emailAddress string to look for in the email address
     * @return [Result] with a list of user
     */
    override suspend fun fetchUserByEmailAddress(emailAddress: String): Result<List<User>>  {
        return try {
            Result.Success(userDao.getUsersFromEmail(emailAddress))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Fetch the [UserPreferences] of a specific user
     *
     * @param userId ID of the user
     * @return [Result] of the operation with a [UserPreferences] object
     */
    suspend fun fetchUserPreferences(userId: String): Result<UserPreferences?>{
        return try {
            Result.Success(userPreferencesDao.getUserPreferences(userId))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}