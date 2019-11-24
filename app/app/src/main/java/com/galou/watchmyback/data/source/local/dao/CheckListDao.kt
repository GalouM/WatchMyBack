package com.galou.watchmyback.data.source.local.dao

import androidx.room.*
import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.utils.CHECK_LIST_TABLE_NAME
import com.galou.watchmyback.utils.CHECK_LIST_TABLE_USER_UUID
import com.galou.watchmyback.utils.CHECK_LIST_TABLE_UUID

/**
 * List all the possible actions on the [CheckList] table
 *
 * @property database reference to the database of the application
 *
 * @see Dao
 * @see CheckList
 */
@Dao
abstract class CheckListDao(private val database: WatchMyBackDatabase) {

    /**
     * Query all the [CheckList] of a specific [User]
     *
     * @param userId ID of the [User] to query
     * @return List of [CheckList] own by this [User]
     *
     * @see Query
     */
    @Query("SELECT * FROM $CHECK_LIST_TABLE_NAME WHERE $CHECK_LIST_TABLE_USER_UUID = :userId")
    abstract suspend fun getUserCheckList(userId: String): List<CheckListWithItems>

    /**
     * Query a [CheckList] and all its [ItemCheckList]
     *
     * @param checkListId ID of the [CheckList] to query
     * @return List of [CheckListWithItems]
     *
     * @see Query
     * @see Transaction
     * @see CheckListWithItems
     */
    @Transaction
    @Query("SELECT * FROM $CHECK_LIST_TABLE_NAME WHERE $CHECK_LIST_TABLE_UUID = :checkListId")
    abstract suspend fun getCheckListWithItems(checkListId: String): CheckListWithItems?

    /**
     * Create a [CheckList] object in the database
     *
     * If an object with the same Primary key exist in the database, it will be replace by this one
     *
     * @param checkList [CheckList] object to create
     *
     * @see Insert
     * @see OnConflictStrategy.REPLACE
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun createCheckList(checkList: CheckList)

    /**
     * Update a [CheckList] object in the database
     *
     * @param checkList [CheckList] object to update
     *
     * @see Update
     */
    @Update
    abstract suspend fun updateCheckList(checkList: CheckList)

    /**
     * Delete a [CheckList] object from the database
     *
     * @param checkList  [CheckList] to delete
     *
     * @see Delete
     */
    @Delete
    abstract suspend fun deleteCheckList(checkList: CheckList)

    @Query("DELETE FROM $CHECK_LIST_TABLE_NAME WHERE $CHECK_LIST_TABLE_USER_UUID = :userId")
    abstract fun deleteUserCheckList(userId: String)


    /**
     * Create a [CheckList] object and its data in the database
     *
     * @param checkList [CheckList] to create
     * @param items List [ItemCheckList] object to create
     *
     * @see Transaction
     * @see ItemCheckList
     * @see CheckListDao.createCheckList
     * @see ItemCheckListDao.insertItemInCheckList
     */
    @Transaction
    open suspend fun createCheckListAndItems(checkList: CheckList, items: List<ItemCheckList>){
        createCheckList(checkList)
        database.itemCheckListDao().insertItemInCheckList(items)
    }

    /**
     * Update a [CheckList] and its data in the database
     *
     * @param checkList [CheckList] object to update
     * @param items List of [ItemCheckList] to update
     *
     * @see Transaction
     * @see ItemCheckList
     * @see ItemCheckListDao.updateItems
     * @see ItemCheckListDao.removeListItems
     * @see CheckListDao.updateCheckList
     */
    @Transaction
    open suspend fun updateCheckListAndItems(checkList: CheckList, items: List<ItemCheckList>){
        database.itemCheckListDao().removeListItems(checkList.id)
        database.itemCheckListDao().insertItemInCheckList(items)
        updateCheckList(checkList)

    }

}