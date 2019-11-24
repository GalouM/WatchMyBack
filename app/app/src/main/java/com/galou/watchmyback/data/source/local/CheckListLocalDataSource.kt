package com.galou.watchmyback.data.source.local

import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.data.source.CheckListDataSource
import com.galou.watchmyback.data.source.local.dao.CheckListDao
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.displayData
import java.lang.Exception

/**
 * @author galou
 * 2019-11-19
 */
class CheckListLocalDataSource(private val checkListDao: CheckListDao) : CheckListDataSource {

    override suspend fun fetchUserCheckLists(userId: String): Result<List<CheckListWithItems>> {
        return try {
            Result.Success(checkListDao.getUserCheckList(userId))
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    override suspend fun fetchCheckList(checkList: CheckList): Result<CheckListWithItems?> {
        return try {
            Result.Success(checkListDao.getCheckListWithItems(checkList.id))
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    override suspend fun createCheckList(
        vararg checkLists: CheckListWithItems
    ): Result<Void?> {
        return try {
            checkLists.forEach {
                checkListDao.createCheckListAndItems(it.checkList, it.items)
            }
            Result.Success(null)
        } catch (e: Exception){
            Result.Error(e)
        }

    }

    override suspend fun updateCheckList(
        checkList: CheckList,
        items: List<ItemCheckList>
    ): Result<Void?> {
        return try {
            checkListDao.updateCheckListAndItems(checkList, items)
            Result.Success(null)
        } catch (e: Exception){
            displayData("$e")
            Result.Error(e)
        }
    }

    override suspend fun deleteCheckList(checkList: CheckListWithItems): Result<Void?> {
        return try {
            checkListDao.deleteCheckList(checkList.checkList)
            Result.Success(null)
        } catch (e: Exception){
            Result.Error(e)
        }
    }
}