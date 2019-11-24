package com.galou.watchmyback.data.source

import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-11-19
 */
interface CheckListDataSource {

    suspend fun fetchUserCheckLists(userId: String): Result<List<CheckListWithItems>>

    suspend fun fetchCheckList(checkList: CheckList): Result<CheckListWithItems?>

    suspend fun createCheckList(vararg checkLists: CheckListWithItems): Result<Void?>

    suspend fun updateCheckList(checkList: CheckList, items: List<ItemCheckList>): Result<Void?>

    suspend fun deleteCheckList(checkList: CheckListWithItems): Result<Void?>
}