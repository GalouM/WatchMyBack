package com.galou.watchmyback.data.source.remote

import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.data.source.CheckListDataSource
import com.galou.watchmyback.utils.CHECKLIST_COLLECTION_NAME
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.await
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Implementation of [CheckListDataSource] for the remote database
 *
 * List all the possible actions on the remote database for the [CheckList] objects
 *
 * @param remoteDB reference toward the remote database
 */
class CheckListRemoteDataSource(
    remoteDB: FirebaseFirestore
) : CheckListDataSource {

    private val ioDispatcher = Dispatchers.IO
    private val checkListCollection = remoteDB.collection(CHECKLIST_COLLECTION_NAME)

    /**
     * Fetch all the [CheckList] and their [ItemCheckList] of a specific user from the database
     *
     * @param userId ID of the user
     * @return [Result] of the action containing a list of [CheckListWithItems]
     */
    override suspend fun fetchUserCheckLists(userId: String): Result<List<CheckListWithItems>> = withContext(ioDispatcher) {
        return@withContext when(val fetchCheckLists = checkListCollection
                .whereEqualTo("checkList.userId", userId)
                .get().await()){
                is Result.Success -> Result.Success(
                        fetchCheckLists.data.toObjects(CheckListWithItems::class.java)
                    )

                is Result.Error -> Result.Error(fetchCheckLists.exception)
                is Result.Canceled -> Result.Canceled(fetchCheckLists.exception)
            }
    }

    /**
     * Fetch a specific [CheckList] with its [ItemCheckList] from the database
     *
     * @param checkListId  ID of the checklist to fetch
     * @return [Result] of the action containing a [CheckListWithItems] object
     */
    override suspend fun fetchCheckList(checkListId: String): Result<CheckListWithItems?> = withContext(ioDispatcher) {
        return@withContext when(val fetchCheckList = checkListCollection
            .document(checkListId)
            .get().await()){
            is Result.Success -> Result.Success(fetchCheckList.data.toObject(CheckListWithItems::class.java))
            is Result.Error -> Result.Error(fetchCheckList.exception)
            is Result.Canceled -> Result.Canceled(fetchCheckList.exception)
        }
    }

    /**
     * Create one of several [CheckList] with their [ItemCheckList] in the database
     *
     * @param checkLists [CheckListWithItems] to create
     * @return [Result] of the operation
     */
    override suspend fun createCheckList(
        vararg checkLists: CheckListWithItems
    ): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            var error = false
            coroutineScope {
                checkLists.forEach { checkList ->
                    launch {
                        when(checkListCollection.document(checkList.checkList.id).set(checkList).await()){
                            is Result.Error, is Result.Canceled -> error = true
                        }
                    }
                }
            }
            when(error){
                true -> Result.Error(Exception("Error while creating checklist"))
                false -> Result.Success(null)
            }
        } catch (e: Exception){
            Result.Error(e)
        }

    }

    /**
     * Update a specific [CheckList] with its [ItemCheckList] in the database
     *
     * It will delete all the checklist's items previously created before creating the new ones
     *
     * @param checkList [CheckList] to udpate
     * @param items [ItemCheckList] of the check list
     * @return [Result] of the operation
     */
    override suspend fun updateCheckList(
        checkList: CheckList,
        items: List<ItemCheckList>
    ): Result<Void?> = withContext(ioDispatcher) {
        return@withContext checkListCollection.document(checkList.id)
            .update("checkList.tripType", checkList.tripType,
                "checkList.name", checkList.name,
                "items", items
            ).await()

    }

    /**
     * Delete a specific [CheckList] and its [ItemCheckList] in the database
     *
     * @param checkList [CheckList] to delete
     * @return [Result] of the operation
     */
    override suspend fun deleteCheckList(checkList: CheckListWithItems): Result<Void?> = withContext(ioDispatcher) {
        return@withContext checkListCollection.document(checkList.checkList.id).delete().await()

    }

    /**
     * Delete all the checklist of a specific user from the database
     *
     * @param userId
     * @return [Result] of the operation
     */
    override suspend fun deleteUserChecklists(userId: String): Result<Void?> {
        return when (val fetchChecklistsTask = fetchUserCheckLists(userId)){
            is Result.Success -> deleteCheckLists(fetchChecklistsTask.data)
            is Result.Error -> Result.Error(fetchChecklistsTask.exception)
            is Result.Canceled -> Result.Canceled(fetchChecklistsTask.exception)
        }
    }

    private suspend fun deleteCheckLists(checklists: List<CheckListWithItems>): Result<Void?> = coroutineScope {
        var error = false
        checklists.forEach {
           launch {
               when(val deleteTask = deleteCheckList(it)){
                   is Result.Error, is Result.Canceled -> error = true
               }
           }
        }

        return@coroutineScope when(error){
            true -> Result.Error(Exception("error while deleting checklist"))
            false -> Result.Success(null)
        }
    }
}