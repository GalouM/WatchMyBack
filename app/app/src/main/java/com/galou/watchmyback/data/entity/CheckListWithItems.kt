package com.galou.watchmyback.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.galou.watchmyback.utils.CHECK_LIST_TABLE_UUID
import com.galou.watchmyback.utils.ITEM_TABLE_LIST_ID

/**
 * Represent a [CheckList] and its [ItemCheckList]
 *
 * [Embedded] object with its [Relation] so Room can return a [CheckList] with all the data connected to it
 *
 * @property checkList
 * @property items items of the check list
 *
 * @see CheckList
 * @see ItemCheckList
 * @see Embedded
 * @see Relation
 *
 * @author Galou Minisini
 */
data class CheckListWithItems(
    @Embedded val checkList: CheckList = CheckList(),
    @Relation(
        parentColumn = CHECK_LIST_TABLE_UUID,
        entityColumn = ITEM_TABLE_LIST_ID,
        entity = ItemCheckList::class
    )
    val items: List<ItemCheckList> = listOf()
)