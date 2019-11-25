package com.galou.watchmyback.data.repository

import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.data.source.local.CheckListLocalDataSource
import com.galou.watchmyback.data.source.remote.CheckListRemoteDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.displayData
import com.galou.watchmyback.utils.returnSuccessOrError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Implementation of [CheckListRepository]
 *
 * List all the possible actions on [CheckList] object
 *
 * @property localSource access to the local database
 * @property remoteSource access to the remote database
 */
class CheckListRepositoryImpl(
    private val localSource: CheckListLocalDataSource,
    private val remoteSource: CheckListRemoteDataSource
) : CheckListRepository {

    override var checkList: CheckList? = null
    override var checkListFetched: Boolean = false

    /**
     * Fetch all the [CheckListWithItems] of a specific user
     *
     * If refresh is true the data will we fetched from the remote database and
     * the local database will be updated.
     * If it fails or if refresh is false the data will be pulled from the local database
     *
     * @param userId ID if the user
     * @param refresh refresh parameter
     * @return [Result] of the operation with a list of [CheckListWithItems]
     *
     * @see CheckListLocalDataSource.fetchUserCheckLists
     * @see CheckListRemoteDataSource.fetchUserCheckLists
     * @see CheckListLocalDataSource.deleteExistingChecklist
     * @see CheckListLocalDataSource.createCheckList
     */
    override suspend fun fetchUserCheckLists(userId: String, refresh: Boolean): Result<List<CheckListWithItems>> {
        return when(refresh){
            true -> {
                when(val remoteResult = remoteSource.fetchUserCheckLists(userId)){
                    is Result.Success -> {
                        localSource.deleteExistingChecklist(userId)
                        localSource.createCheckList(*remoteResult.data.toTypedArray())
                        remoteResult
                    }
                    is Result.Error, is Result.Canceled -> localSource.fetchUserCheckLists(userId)
                }
            }
            false -> localSource.fetchUserCheckLists(userId)
        }
    }

    /**
     * Fetch a specific [CheckList] and its [ItemCheckList] from the database
     *
     * If refresh is true the data will we fetched from the remote database.
     * If it fails or if refresh is false the data will be pulled from the local database
     *
     * @param checkList [CheckList] to fetch
     * @param refresh refresh parameter
     * @return [Result] of the operation with a [CheckListWithItems] object
     *
     * @see CheckListRemoteDataSource.fetchCheckList
     * @see CheckListLocalDataSource.fetchCheckList
     */
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

    /**
     * Create a [CheckList] and its [ItemCheckList] in the databases
     *
     * @param checkList [CheckList] to create
     * @param items list of [ItemCheckList] to create
     * @return [Result] of the operation
     *
     * @see CheckListLocalDataSource.createCheckList
     * @see CheckListRemoteDataSource.createCheckList
     */
    override suspend fun createCheckList(
        checkList: CheckList,
        items: List<ItemCheckList>
    ): Result<Void?> = coroutineScope {
        val checkListWithItems = CheckListWithItems(checkList = checkList, items = items)
        val localTask = async { localSource.createCheckList(checkListWithItems) }
        val remoteTask = async { remoteSource.createCheckList(checkListWithItems) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }

    /**
     * Update a specific [CheckList] and its [ItemCheckList] in the databases
     *
     * @param checkList [CheckList] to update
     * @param items list of [ItemCheckList]
     * @return [Result] of the operation
     *
     * @see CheckListLocalDataSource.updateCheckList
     * @see CheckListRemoteDataSource.updateCheckList
     */
    override suspend fun updateCheckList(
        checkList: CheckList,
        items: List<ItemCheckList>
    ): Result<Void?>  = coroutineScope {
        val localTask = async { localSource.updateCheckList(checkList, items) }
        val remoteTask = async { remoteSource.updateCheckList(checkList, items) }
        displayData("remote: ${remoteTask.await()} local: ${localTask.await()}")
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }

    /**
     * Delete a checklist from the databases
     *
     * @param checkList [CheckListWithItems] to delete
     * @return [Result] of the operation
     *
     * @see CheckListLocalDataSource.deleteCheckList
     * @see CheckListRemoteDataSource.deleteCheckList
     */
    override suspend fun deleteCheckList(checkList: CheckListWithItems): Result<Void?> = coroutineScope {
        val localTask = async { localSource.deleteCheckList(checkList) }
        val remoteTask = async { remoteSource.deleteCheckList(checkList) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }
}