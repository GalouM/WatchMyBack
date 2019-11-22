package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.data.source.local.CheckListLocalDataSource
import com.galou.watchmyback.data.source.remote.CheckListRemoteDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.returnSuccessOrError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * @author galou
 * 2019-11-20
 */
class CheckListRepositoryImpl(
    private val localSource: CheckListLocalDataSource,
    private val remoteSource: CheckListRemoteDataSource
) : CheckListRepository {

    override var checkList: CheckList? = null
    override var checkListFetched: Boolean = false

    override suspend fun fetchUserCheckLists(userId: String, refresh: Boolean): Result<List<CheckListWithItems>> {
        return when(refresh){
            true -> {
                when(val remoteResult = remoteSource.fetchUserCheckLists(userId)){
                    is Result.Success -> {
                        localSource.createCheckList(*remoteResult.data.toTypedArray())
                        remoteResult
                    }
                    is Result.Error, is Result.Canceled -> localSource.fetchUserCheckLists(userId)
                }
            }
            false -> localSource.fetchUserCheckLists(userId)
        }
    }

    override suspend fun fetchCheckList(checkList: CheckList, refresh: Boolean): Result<CheckListWithItems?>  {
        return when(refresh){
            true -> {
                when(val remoteResult = remoteSource.fetchCheckList(checkList)){
                    is Result.Success -> remoteResult
                    is Result.Error, is Result.Canceled -> localSource.fetchCheckList(checkList)
                }
            }
            false -> localSource.fetchCheckList(checkList)
        }
    }

    override suspend fun createCheckList(
        checkList: CheckList,
        items: List<ItemCheckList>
    ): Result<Void?> = coroutineScope {
        val checkListWithItems = CheckListWithItems(checkList = checkList, items = items)
        val localTask = async { localSource.createCheckList(checkListWithItems) }
        val remoteTask = async { remoteSource.createCheckList(checkListWithItems) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }

    override suspend fun updateCheckList(
        checkList: CheckList,
        items: List<ItemCheckList>
    ): Result<Void?>  = coroutineScope {
        val localTask = async { localSource.updateCheckList(checkList, items) }
        val remoteTask = async { remoteSource.updateCheckList(checkList, items) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }

    override suspend fun deleteCheckList(checkList: CheckList): Result<Void?> = coroutineScope {
        val localTask = async { localSource.deleteCheckList(checkList) }
        val remoteTask = async { remoteSource.deleteCheckList(checkList) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }
}