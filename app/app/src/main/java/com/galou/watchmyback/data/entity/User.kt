package com.galou.watchmyback.data.entity

import androidx.room.*
import com.galou.watchmyback.utils.*

/**
 * Represent a User of the application
 *
 * @property id unique id generated randomly use to identify a user
 * @property email the user's email
 * @property username the user's name in the application
 * @property phoneNumber the user's phone number
 *
 * @see Entity
 *
 * @author Galou Minisini
 */
@Entity(
    tableName = USER_TABLE_NAME,
    indices = [Index(value = [USER_TABLE_USERNAME], unique = false), Index(value = [USER_TABLE_EMAIL], unique = true)]
)
data class User (
    @ColumnInfo(name = USER_TABLE_UUID) @PrimaryKey var id: String = idGenerated,
    @ColumnInfo(name = USER_TABLE_EMAIL) var email: String? = "",
    @ColumnInfo(name = USER_TABLE_USERNAME) var username: String? = "",
    @ColumnInfo(name = USER_TABLE_PHONE_NUMBER) var phoneNumber: String? = "",
    @ColumnInfo(name = USER_TABLE_PICTURE_URL) var pictureUrl: String? = "",
    @Ignore val friendsId: MutableList<String> = mutableListOf()
)