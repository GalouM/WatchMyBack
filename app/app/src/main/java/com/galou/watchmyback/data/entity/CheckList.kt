package com.galou.watchmyback.data.entity

import androidx.room.*
import com.galou.watchmyback.utils.*

/**
 * Created by galou on 2019-10-20
 */
@Entity(
    tableName = CHECK_LIST_TABLE_NAME,
    indices = [Index(value = [CHECK_LIST_TABLE_TRIP_TYPE], unique = false)],
    foreignKeys = [
    ForeignKey(
        entity = User::class,
        parentColumns = [USER_TABLE_UUID],
        childColumns = [CHECK_LIST_TABLE_USER_UUID],
        onDelete = ForeignKey.CASCADE
    )]
)
data class CheckList(
    @ColumnInfo(name = CHECK_LIST_TABLE_UUID) @PrimaryKey var uuid: String = idGenerated,
    @ColumnInfo(name = CHECK_LIST_TABLE_USER_UUID) var userId: String = "",
    @ColumnInfo(name = CHECK_LIST_TABLE_TRIP_TYPE) var tripType: TripType? = null
)