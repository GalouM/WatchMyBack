package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.galou.watchmyback.utils.*

/**
 * Created by galou on 2019-10-20
 */
@Entity(
    tableName = USER_TABLE_NAME,
    indices = [Index(value = [USER_TABLE_USERNAME], unique = false), Index(value = [USER_TABLE_EMAIL], unique = true)]
)
data class User (
    @ColumnInfo(name = USER_TABLE_USERNAME) @PrimaryKey var uuid: String = idGenerated,
    @ColumnInfo(name = USER_TABLE_EMAIL) var email: String = "",
    @ColumnInfo(name = USER_TABLE_USERNAME) var username: String = "",
    @ColumnInfo(name = USER_TABLE_PHONE_NUMBER) var phoneNumber: String = ""
)