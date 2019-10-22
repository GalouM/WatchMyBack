package com.galou.watchmyback.data.database.dao

import androidx.room.*
import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.utils.ITEM_LIST_TABLE_NAME
import com.galou.watchmyback.utils.ITEM_TABLE_LIST_ID
import com.galou.watchmyback.utils.ITEM_TABLE_UUID

/**
 * List all the actions possible on the [ItemCheckList] table
 *
 * @see Dao
 * @see ItemCheckList
 *
 * @author Galou Minisini
 *
 */
@Dao
interface ItemCheckListDao {

    /**
     * Create a list of [ItemCheckList] object in the database
     *
     * If an object with the same Primary key exist in the database, it will be replace by this one
     *
     * @param items List of Items to create
     *
     * @see Insert
     * @see OnConflictStrategy.REPLACE
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemInCheckList(items: List<ItemCheckList>)

    /**
     * Delete all the [ItemCheckList] that are part of a specific [CheckList]
     *
     * @param checkListId ID of the checklist from which the items have to be deleted
     *
     * @see Query
     * @see CheckList
     */
    @Query("DELETE FROM $ITEM_LIST_TABLE_NAME WHERE $ITEM_TABLE_LIST_ID = :checkListId")
    suspend fun removeListItems(checkListId: String)

    /**
     * Update an [ItemCheckList] object
     *
     * @param items [ItemCheckList] object to update
     *
     * @see Update
     */
    @Update
    suspend fun updateItems(items: List<ItemCheckList>)

}