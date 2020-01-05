package com.galou.watchmyback.data.source.local

import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.data.source.CheckListDataSource
import com.galou.watchmyback.data.source.local.dao.CheckListDao
import com.galou.watchmyback.utils.Result
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Implementation of [CheckListDataSource] for the local database
 *
 * List all the possible actions on the local database for the [CheckList] objects
 *
 * @property checkListDao [CheckListDao]
 */
class CheckListLocalDataSource(private val checkListDao: CheckListDao) : CheckListDataSource {

    /**
     * Fetch all the [CheckList] of a specific user from the database
     *
     * @param userId ID of the user
     * @return [Result] of the action containing a list of [CheckListWithItems]
     *
     * @see CheckListDao.getUserCheckList
     */
    override suspend fun fetchUserCheckLists(userId: String): Result<List<CheckListWithItems>> {
        return try {
            Result.Success(checkListDao.getUserCheckList(userId))
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    /**
     * Fetch a specific [CheckList] with its [ItemCheckList] from the database
     *
     * @param checkListId checklist to fetch
     * @return [Result] of the action containing a [CheckListWithItems] object
     *
     * @see CheckListDao.getCheckListWithItems
     */
    override suspend fun fetchCheckList(checkListId: String): Result<CheckListWithItems?> {
        return try {
            Result.Success(checkListDao.getCheckListWithItems(checkListId))
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    /**
     * Create one of several [CheckList] with their [ItemCheckList] in the database
     *
     * @param checkLists [CheckListWithItems] to create
     * @return [Result] of the operation
     *
     * @see CheckListDao.createCheckListAndItems
     */
    override suspend fun createCheckList(
        vararg checkLists: CheckListWithItems
    ): Result<Void?> {
        return try {
            coroutineScope {
                checkLists.forEach {
                    launch { checkListDao.createCheckListAndItems(it.checkList, it.items) }
                }
            }
            Result.Success(null)
        } catch (e: Exception){
            Result.Error(e)
        }

    }

    /**
     * Update a specific [CheckList] with its [ItemCheckList] in the database
     *
     * @param checkList [CheckList] to udpate
     * @param items [ItemCheckList] of the check list
     * @return [Result] of the operation
     *
     * @see CheckListDao.updateCheckListAndItems
     */
    override suspend fun updateCheckList(
        checkList: CheckList,
        items: List<ItemCheckList>
    ): Result<Void?> {
        return try {
            checkListDao.updateCheckListAndItems(checkList, items)
            Result.Success(null)
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    /**
     * Delete a specific [CheckList] in the database
     *
     * @param checkList [CheckList] to delete
     * @return [Result] of the operation
     *
     * @see CheckListDao.deleteCheckList
     */
    override suspend fun deleteCheckList(checkList: CheckListWithItems): Result<Void?> {
        return try {
            checkListDao.deleteCheckList(checkList.checkList)
            Result.Success(null)
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    /**
     * Delete all the checklist of a specific user from the database
     *
     * @param userId
     * @return [Result] of the operation
     */
    override suspend fun deleteUserChecklists(userId: String): Result<Void?> {
        return try {
            checkListDao.deleteUserCheckList(userId)
            Result.Success(null)
        } catch (e: Exception){
            Result.Error(e)
        }
    }
}