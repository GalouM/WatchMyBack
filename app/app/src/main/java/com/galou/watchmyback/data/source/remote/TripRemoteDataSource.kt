package com.galou.watchmyback.data.source.remote

import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.remoteDBObject.TripWithDataRemoteDB
import com.galou.watchmyback.data.source.TripDataSource
import com.galou.watchmyback.utils.CHECKLIST_COLLECTION_NAME
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.TRIP_COLLECTION_NAME
import com.galou.watchmyback.utils.await
import com.galou.watchmyback.utils.extension.convertForRemoteDB
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author galou
 * 2019-12-08
 */
class TripRemoteDataSource(
    remoteDB: FirebaseFirestore
) : TripDataSource {

    private val ioDispatcher = Dispatchers.IO
    private val tripCollection = remoteDB.collection(TRIP_COLLECTION_NAME)
    private val checkListCollection = remoteDB.collection(CHECKLIST_COLLECTION_NAME)

    /**
     * Create a [TripWithDataRemoteDB] in the database and update the item of a checklist
     *
     * @param trip [TripWithData] to create
     * @param checkList checklist assigned to the trip
     * @return [Result] of the operation
     *
     * @see updateCheckListItems
     */
    override suspend fun createTrip(
        trip: TripWithData,
        checkList: CheckListWithItems?
    ): Result<Void?> = withContext(ioDispatcher) {
        when(val task = tripCollection.document(trip.trip.id).set(trip.convertForRemoteDB()).await()){
            is Result.Success -> {
                checkList?.let { return@withContext updateCheckListItems(checkList) }
                return@withContext Result.Success(null)

            }
            else -> return@withContext task
        }
    }

    /**
     * Upthe the items of a checklist assigned to the trip
     *
     * @param checkList [CheckListWithItems] to update
     * @return [Result] of the operation
     */
    private suspend fun updateCheckListItems(checkList: CheckListWithItems)
            : Result<Void?> = withContext(ioDispatcher) {
        return@withContext checkListCollection.document(checkList.checkList.id)
            .update("items", checkList.items)
            .await()
    }

    /**
     * Fetch and delete all the active trip of a user in the database
     *
     * @param userId Id of the user
     * @return [Result] of the operation
     *
     * @see deleteTrips
     */
    override suspend fun deleteActiveTrip(userId: String): Result<Void?> = withContext(ioDispatcher) {
        return@withContext when(val result = tripCollection
            .whereEqualTo("trip.userId", userId)
            .whereEqualTo("trip.active", true)
            .get().await()){
            is Result.Success -> deleteTrips(result.data.toObjects(TripWithDataRemoteDB::class.java))
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Delete a list of trips
     *
     * @param trips [TripWithDataRemoteDB] to delete
     * @return [Result] of the operation
     */
    private suspend fun deleteTrips(trips: List<TripWithDataRemoteDB>): Result<Void?> = coroutineScope {
        var error = false
        trips.forEach { trip ->
            launch {
                when(tripCollection.document(trip.trip.id).delete().await()){
                    is Result.Error -> error = true
                }
            }
        }

        return@coroutineScope when(error){
            true -> Result.Error(Exception("Error while deleting trip"))
            false -> Result.Success(null)
        }
    }
}