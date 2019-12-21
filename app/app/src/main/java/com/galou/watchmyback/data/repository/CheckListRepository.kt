package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-11-20
 */
interface CheckListRepository {

    var checkList: CheckList?
    var checkListFetched: Boolean

    suspend fun fetchUserCheckLists(userId: String, refresh: Boolean): Result<List<CheckListWithItems>>

    suspend fun fetchCheckList(checkListId: String, refresh: Boolean): Result<CheckListWithItems?>

    suspend fun createCheckList(checkList: CheckList, items: List<ItemCheckList>): Result<Void?>

    suspend fun updateCheckList(checkList: CheckList, items: List<ItemCheckList>): Result<Void?>

    suspend fun deleteCheckList(checkList: CheckListWithItems): Result<Void?>
}