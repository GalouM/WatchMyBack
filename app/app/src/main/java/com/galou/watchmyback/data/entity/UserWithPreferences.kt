package com.galou.watchmyback.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.galou.watchmyback.utils.PREFERENCES_TABLE_USER_UUID
import com.galou.watchmyback.utils.USER_TABLE_UUID

/**
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