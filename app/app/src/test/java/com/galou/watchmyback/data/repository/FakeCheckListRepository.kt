package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.testHelpers.checkLists
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-11-24
 */
class FakeCheckListRepository : CheckListRepository {

    override var checkList: CheckList? = null
    override var checkListFetched: Boolean = false

    override suspend fun fetchUserCheckLists(
        userId: String,
        refresh: Boolean
    ): Result<List<CheckListWithItems>> {
        val checkListsWithItems = checkLists.filter { it.checkList.userId == userId }
        return Result.Success(checkListsWithItems)
    }

    override suspend fun fetchCheckList(
        checkListId: String,
        refresh: Boolean
    ): Result<CheckListWithItems?> {
        val checkListWithItems = checkLists.find { it.checkList.id == checkListId }
        return Result.Success(checkListWithItems)
    }

    override suspend fun createCheckList(
        checkList: CheckList,
        items: List<ItemCheckList>
    ): Result<Void?> = Result.Success(null)

    override suspend fun updateCheckList(
        checkList: CheckList,
        items: List<ItemCheckList>
    ): Result<Void?> = Result.Success(null)

    override suspend fun deleteCheckList(checkList: CheckListWithItems): Result<Void?> = Result.Success(null)
}