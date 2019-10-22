package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.galou.watchmyback.utils.FRIEND_TABLE_NAME
import com.galou.watchmyback.utils.FRIEND_TABLE_USER_FRIEND_UUID
import com.galou.watchmyback.utils.FRIEND_TABLE_USER_UUID
import com.galou.watchmyback.utils.USER_TABLE_UUID

/**
 * Represent a Friend of a [User]
 *
 * A [User] can be friend with another [User]
 * This is Ad Hoc table to connect two [User]
 *
 * @property userId ID of the [User] to whom the friend belongs
 * @property friendId ID of the friend.
 *
 * @see User
 *
 * @author Galou Minisini
 */
@Entity(
    tableName = FRIEND_TABLE_NAME,
    indices = [Index(value = [FRIEND_TABLE_USER_UUID, FRIEND_TABLE_USER_FRIEND_UUID], unique = true)],
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