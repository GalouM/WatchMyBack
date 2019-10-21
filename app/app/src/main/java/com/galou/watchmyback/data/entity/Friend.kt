package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.galou.watchmyback.utils.FRIEND_TABLE_NAME
import com.galou.watchmyback.utils.FRIEND_TABLE_USER_FRIEND_UUID
import com.galou.watchmyback.utils.FRIEND_TABLE_USER_UUID
import com.galou.watchmyback.utils.USER_TABLE_UUID

/**
 * Created by galou on 2019-10-20
 */
@Entity(
    tableName = FRIEND_TABLE_NAME,
    foreignKeys = [
    ForeignKey(
        entity = User::class,
        parentColumns = [USER_TABLE_UUID],
        childColumns = [FRIEND_TABLE_USER_UUID],
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = User::class,
        parentColumns = [USER_TABLE_UUID],
        childColumns = [FRIEND_TABLE_USER_FRIEND_UUID],
        onDelete = ForeignKey.CASCADE
    )],
    primaryKeys = [FRIEND_TABLE_USER_UUID, FRIEND_TABLE_USER_FRIEND_UUID]
)
data class Friend(
    @ColumnInfo(name = FRIEND_TABLE_USER_UUID) var userId: String = "",
    @ColumnInfo(name = FRIEND_TABLE_USER_FRIEND_UUID) var friendId: String = ""
)