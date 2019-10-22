package com.galou.watchmyback.data.database.dao

import androidx.room.*
import com.galou.watchmyback.data.database.WatchMyBackDatabase
import com.galou.watchmyback.data.entity.Trip
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.*

/**
 * Created by galou on 2019-10-21
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM $USER_TABLE_NAME WHERE $USER_TABLE_UUID = :userId")
    suspend fun getUser(userId: String): List<User>

    @Query("SELECT * FROM $USER_TABLE_NAME WHERE $USER_TABLE_USERNAME LIKE :username")
    suspend fun getUsersFromUsername(username: String): List<User>

    @Query("SELECT * FROM $USER_TABLE_NAME WHERE $USER_TABLE_UUID IN (:userIds)")
    suspend fun getUsers(userIds: List<String>): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM $USER_TABLE_NAME WHERE $USER_TABLE_UUID = :userId")
    suspend fun deleteUser(userId: String)

}