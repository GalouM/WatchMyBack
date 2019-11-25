package com.galou.watchmyback.data.source.remote

import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.ItemCheckList
import com.galou.watchmyback.data.source.CheckListDataSource
import com.galou.watchmyback.utils.*
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
    private val itemCollection = remoteDB.collection(ITEM_COLLECTION_NAME)

    /**
     * Fetch all the [CheckList] and their [ItemCheckList] of a specific user from the database
     *
     * @param userId ID of the user
     * @return [Result] of the action containing a list of [CheckListWithItems]
     *
     * @see fetchItemsCheckList
     */
    override suspend fun fetchUserCheckLists(userId: String): Result<List<CheckListWithItems>> = withContext(ioDispatcher) {
        return@withContext when(val fetchCheckLists = checkListCollection
                .whereEqualTo("userId", userId)
                .get().await()){
                is Result.Success -> {
                    val checkListWithItems = mutableListOf<CheckListWithItems>()
                    fetchCheckLists.data.toObjects(CheckList::class.java).forEach { checkList ->
                        checkListWithItems.add(CheckListWithItems(
                            checkList = checkList,
                            items = fetchItemsCheckList(checkList) ?: mutableListOf()
                        ))
                    }
                    Result.Success(checkListWithItems)

                }
                is Result.Error -> Result.Error(fetchCheckLists.exception)
                is Result.Canceled -> Result.Canceled(fetchCheckLists.exception)
            }
    }

    /**
     * Fetch a specific [CheckList] with its [ItemCheckList] from the database
     *
     * @param checkList checklist to fetch
     * @return [Result] of the action containing a [CheckListWithItems] object
     *
     * @see fetchItemsCheckList
     */
    override suspend fun fetchCheckList(checkList: CheckList): Result<CheckListWithItems?> = withContext(ioDispatcher) {
        return@withContext when(val items = fetchItemsCheckList(checkList)){
            null  -> Result.Error(Exception("Error while fetching list's items"))
            else -> Result.Success(CheckListWithItems(
                checkList = checkList,
                items = items.toMutableList()
            ))
        }
    }

    /**
     * Create one of several [CheckList] with their [ItemCheckList] in the database
     *
     * @param checkLists [CheckListWithItems] to create
     * @return [Result] of the operation
     *
     * @see createItemsCheckList
     */
    override suspend fun createCheckList(
        vararg checkLists: CheckListWithItems
    ): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            var error = false
            coroutineScope {
                checkLists.forEach { checkList ->
                    launch {
                        when(checkListCollection.document(checkList.checkList.id).set(checkList.checkList).await()){
                            is Result.Error, is Result.Canceled -> error = true
                        }
                        when (createItemsCheckList(checkList.items)){
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
     *
     * @see deleteItemsCheckList
     * @see createItemsCheckList
     */
    override suspend fun updateCheckList(
        checkList: CheckList,
        items: List<ItemCheckList>
    ): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            var error = false
            coroutineScope {
                launch {
                    when(checkListCollection.document(checkList.id)
                        .update("tripType", checkList.tripType,
                            "name", checkList.name).await()){
                        is Result.Error, is Result.Canceled -> error = true
                    }
                }
                launch {
                    when(val task = deleteItemsCheckList(items)){
                        is Result.Canceled, is Result.Error -> error = true
                        is Result.Success -> {
                            when(createItemsCheckList(items)){
                                is Result.Error, is Result.Canceled -> error = true
                            }
                        }
                    }
                }
            }
            when(error){
                true -> Result.Error(Exception("Error while updating checklist"))
                false -> Result.Success(null)
            }
        } catch (e: Exception){
            Result.Error(e)
        }

    }

    /**
     * Delete a specific [CheckList] and its [ItemCheckList] in the database
     *
     * @param checkList [CheckList] to delete
     * @return [Result] of the operation
     *
     * @see deleteItemsCheckList
     */
    override suspend fun deleteCheckList(checkList: CheckListWithItems): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            var error = false
            coroutineScope {
                launch {
                    when(checkListCollection.document(checkList.checkList.id).delete().await()){
                        is Result.Error, is Result.Canceled -> error = true
                    }
                }
                launch {
                    when(deleteItemsCheckList(checkList.items)){
                        is Result.Error, is Result.Canceled -> error = true
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
     * Fetch all the [ItemCheckList] of a specific checklist
     *
     * @param checkList check list to query
     * @return a list of [ItemCheckList]
     */
    private suspend fun fetchItemsCheckList(checkList: CheckList): List<ItemCheckList>? = withContext(ioDispatcher){
        return@withContext when(val items = itemCollection
            .whereEqualTo("listId", checkList.id)
            .get().await()){
            is Result.Success -> items.data.toObjects(ItemCheckList::class.java)
            is Result.Error, is Result.Canceled  -> null
        }

    }

    /**
     * Delete all the specified items from the database
     *
     * @param items list of [ItemCheckList] to delete
     * @return [Result] of the operation
     */
    private suspend fun deleteItemsCheckList(items: List<ItemCheckList>): Result<Void?> = withContext(ioDispatcher) {
        var error = false
        return@withContext try {
            coroutineScope {
                items.forEach {
                    launch {
                        when (itemCollection.document(it.id).delete().await()) {
                            is Result.Error, is Result.Canceled -> error = true
                        }
                    }
                }
            }
            when (error) {
                true -> Result.Error(Exception("Error while deleting items"))
                false -> Result.Success(null)
            }

        } catch (e: Exception){
            Result.Error(e)
        }
    }

    /**
     * Create all the specified Items in the database
     *
     * @param items list of [ItemCheckList] to create
     * @return [Result] of the operation
     */
    private suspend fun createItemsCheckList(items: List<ItemCheckList>): Result<Void?> = withContext(ioDispatcher){
        var error = false
        items.forEach {
            coroutineScope {
                launch {
                    when(itemCollection.document(it.id).set(it).await()) {
                        is Result.Error, is Result.Canceled -> error = true
                    }
                }
            }
        }
        return@withContext when(error) {
            true -> Result.Error(Exception("Error while creating items"))
            false -> Result.Success(null)
        }
    }
}