package com.galou.watchmyback.data.database.dao

import androidx.room.*
import com.galou.watchmyback.data.database.WatchMyBackDatabase
import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.utils.*

/**
 * Created by galou on 2019-10-21
 */

@Dao
abstract class CheckListDao(private val database: WatchMyBackDatabase) {

    @Query("SELECT * FROM $CHECK_LIST_TABLE_NAME WHERE $CHECK_LIST_TABLE_USER_UUID = :userId")
    abstract suspend fun getUserCheckList(userId: String): List<CheckList>

    @Transaction
    @Query("SELECT * FROM $CHECK_LIST_TABLE_NAME WHERE $CHECK_LIST_TABLE_UUID = :checkListId")
    abstract suspend fun getCheckListWithItems(checkListId: String): List<CheckListWithItems>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun createCheckList(checkList: CheckList)

    @Update
    abstract suspend fun updateCheckList(checkList: CheckList)

    @Query("DELETE FROM $CHECK_LIST_TABLE_NAME WHERE $CHECK_LIST_TABLE_UUID = :checkListId")
    abstract suspend fun deleteCheckList(checkListId: String)

    @Transaction
    open suspend fun createCheckListAndItems(checkList: CheckList, items: List<ItemCheckList>){
        createCheckList(checkList)
        database.itemCheckListDao().insertItemInCheckList(items)
    }

    @Transaction
    open suspend fun updateCheckListAndItems(checkList: CheckList, items: List<ItemCheckList>){
        database.itemCheckListDao().removeListItems(checkList.id)
        database.itemCheckListDao().insertItemInCheckList(items)
        updateCheckList(checkList)

    }
}