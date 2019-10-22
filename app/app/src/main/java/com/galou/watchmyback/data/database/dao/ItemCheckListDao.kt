package com.galou.watchmyback.data.database.dao

import androidx.room.*
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.utils.ITEM_LIST_TABLE_NAME
import com.galou.watchmyback.utils.ITEM_TABLE_LIST_ID
import com.galou.watchmyback.utils.ITEM_TABLE_UUID

/**
 * Created by galou on 2019-10-21
 */
@Dao
interface ItemCheckListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemInCheckList(items: List<ItemCheckList>)

    @Query("DELETE FROM $ITEM_LIST_TABLE_NAME WHERE $ITEM_TABLE_LIST_ID = :checkListId")
    suspend fun removeListItems(checkListId: String)

    @Update
    suspend fun updateItems(items: List<ItemCheckList>)

}