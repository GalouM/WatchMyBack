package com.galou.watchmyback.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.galou.watchmyback.utils.PREFERENCES_TABLE_USER_UUID
import com.galou.watchmyback.utils.USER_TABLE_UUID

/**
 * Represent a [User] with his/her [UserWithPreferences]
 *
 * [Embedded] object with its [Relation] so Room can return a [User] with his/her preferences
 *
 * @see Embedded
 * @see Relation
 * @see User
 * @see UserPreferences
 *
 * @author galou
 * 2019-10-31
 */
data class UserWithPreferences(
    @Embedded val user: User,
    @Relation(
        entity = UserPreferences::class,
        parentColumn = USER_TABLE_UUID,
        entityColumn = PREFERENCES_TABLE_USER_UUID
    ) val preferences: UserPreferences?
)