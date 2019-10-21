package com.galou.watchmyback.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.galou.watchmyback.utils.*

/**
 * Created by galou on 2019-10-20
 */

@Entity(
    tableName = ITEM_LIST_TABLE_NAME,
    foreignKeys = [
    ForeignKey(
        entity = CheckList::class,
        parentColumns = [CHECK_LIST_TABLE_UUID],
        childColumns = [ITEM_TABLE_LIST_ID],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ItemCheckList(
    @ColumnInfo(name = ITEM_TABLE_UUID) @PrimaryKey var uuid: String = idGenerated,
    @ColumnInfo(name = ITEM_TABLE_LIST_ID) var listId: String = "",
    @ColumnInfo(name = ITEM_LIST_TABLE_NAME) var name: String = "",
    @ColumnInfo(name = ITEM_TABLE_CHECKED) var checked: Boolean = false
)