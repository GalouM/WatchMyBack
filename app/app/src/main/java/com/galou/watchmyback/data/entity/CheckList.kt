package com.galou.watchmyback.data.entity

import androidx.room.*
import com.galou.watchmyback.utils.*

/**
 * Represent a [User]'s Check List
 *
 * A [User] can create, modify and delete a check list and it's items.
 * Each check list is assigned to a specific [User] and to a [TripType]
 *
 * @property uuid unique id generated randomly use to identify a check list
 * @property userId Id of the [User] who own the check list
 * @property tripType Type of [Trip] for which this check list was created
 *
 * @see TripType
 * @see User
 * @see Trip
 * @see [ItemCheckList]
 *
 * @author Galou Minisini
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