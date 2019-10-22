package com.galou.watchmyback.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.galou.watchmyback.utils.CHECK_LIST_TABLE_UUID
import com.galou.watchmyback.utils.ITEM_TABLE_LIST_ID

/**
 * Created by galou on 2019-10-21
 */
data class CheckListWithItems(
    @Embedded val checkList: CheckList,
    @Relation(parentColumn = CHECK_LIST_TABLE_UUID, entityColumn = ITEM_TABLE_LIST_ID, entity = ItemCheckList::class)
    val items: List<ItemCheckList>
)