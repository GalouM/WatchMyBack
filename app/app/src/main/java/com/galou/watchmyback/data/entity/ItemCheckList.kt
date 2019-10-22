package com.galou.watchmyback.data.entity

import androidx.room.*
import com.galou.watchmyback.utils.*

/**
 * Represent an Item in a [CheckList]
 *
 * Each [CheckList] contains a certain numbers of items.
 * Items can be added, deleted and modify
 * A [User] can specify if he/she took this item with him/her or not by checking it
 *
 * @property id unique id generated randomly use to identify an item
 * @property listId ID of the [CheckList] that contains this item
 * @property name name of the item
 * @property checked tells if the [User] took this item with him/her or not
 *
 * @see CheckList
 *
 * @author Galou Minisini
 */

@Entity(
    tableName = ITEM_LIST_TABLE_NAME,
    indices = [Index(value = [ITEM_TABLE_LIST_ID], unique = false)],
    foreignKeys = [
    ForeignKey(
        entity = CheckList::class,
        parentColumns = [CHECK_LIST_TABLE_UUID],
        childColumns = [ITEM_TABLE_LIST_ID],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ItemCheckList(
    @ColumnInfo(name = ITEM_TABLE_UUID) @PrimaryKey var id: String = idGenerated,
    @ColumnInfo(name = ITEM_TABLE_LIST_ID) var listId: String = "",
    @ColumnInfo(name = ITEM_TABLE_NAME) var name: String = "",
    @ColumnInfo(name = ITEM_TABLE_CHECKED) var checked: Boolean = false
)