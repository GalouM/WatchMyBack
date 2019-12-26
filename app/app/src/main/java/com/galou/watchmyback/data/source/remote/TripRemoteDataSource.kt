package com.galou.watchmyback.data.source.remote

import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.remoteDBObject.TripWithDataRemoteDB
import com.galou.watchmyback.data.source.TripDataSource
import com.galou.watchmyback.utils.*
import com.galou.watchmyback.utils.extension.convertForLocal
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
    private val userCollection = remoteDB.collection(USER_COLLECTION_NAME)

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
     * Update the the items of a checklist assigned to the trip
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
     * Fetch the user active trip from the database
     *
     * @param userId ID of the user
     * @return [Result] of the operation with a [TripWithData]
     *
     * @see fetchActiveTripFromDB
     * @see fetchTripWatchers
     */
    override suspend fun fetchActiveTrip(userId: String): Result<TripWithData?> = withContext(ioDispatcher) {
        return@withContext when(val result = fetchActiveTripFromDB(userId)){
            is Result.Success -> {
                if(result.data.isNotEmpty()){
                    val tripFromDB = result.data[0]
                    when(val resultWatchers = fetchTripWatchers(tripFromDB.watchersId)){
                        is Result.Success -> Result.Success(tripFromDB.convertForLocal(resultWatchers.data))
                        is Result.Error -> Result.Error(resultWatchers.exception)
                        is Result.Canceled -> Result.Canceled(resultWatchers.exception)
                    }
                } else {
                    Result.Success(null)
                }
            }
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Fetch all the active trip of a specific user
     *
     * @param userId ID of the user
     * @return [Result] of the operation with a List of [TripWithDataRemoteDB]
     */
    private suspend fun fetchActiveTripFromDB(userId: String): Result<List<TripWithDataRemoteDB>> = withContext(ioDispatcher){
        return@withContext when(val result = tripCollection
            .whereEqualTo("trip.userId", userId)
            .whereEqualTo("trip.active", true)
            .get().await()) {
            is Result.Success -> Result.Success(result.data.toObjects(TripWithDataRemoteDB::class.java))
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Fetch all the watcher of a trip
     *
     * In the remote Database each trip has a list of userID corresponding to the ID of the users that are watching the trip.
     * We need to fetch the User information to be displayed
     *
     * @param watchersId
     * @return [Result] of operation with a list of user
     */
    private suspend fun fetchTripWatchers(watchersId: List<String>): Result<List<User>> = withContext(ioDispatcher){
        val users = mutableListOf<User?>()
        var error = false
        coroutineScope {
            for (id: String in watchersId){
                launch {
                    when(val result = userCollection.document(id).get().await()){
                        is Result.Success -> users.add(result.data.toObject(User::class.java))
                        else -> error = true
                    }
                }
            }
        }
        return@withContext when(error){
            false -> Result.Success(users.mapNotNull { it })
            true -> Result.Error(Exception("Error while fetching the watchers profile"))
        }

    }

    /**
     * Fetch and delete all the active trip of a user in the database
     *
     * @param userId Id of the user
     * @return [Result] of the operation
     *
     * @see deleteTrips
     * @see fetchActiveTripFromDB
     */
    override suspend fun deleteActiveTrip(userId: String): Result<Void?> = withContext(ioDispatcher) {
        return@withContext when(val result = fetchActiveTripFromDB(userId)){
            is Result.Success -> deleteTrips(result.data)
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

    /**
     * Fetch the [TripWithDataRemoteDB] with the corrsponding ID and convert it into a [TripWithData]
     *
     * @param tripId trip ID to fetch
     * @return [Result] of the operation with a [TripWithData] object
     */
    override suspend fun fetchTrip(tripId: String): Result<TripWithData?> = withContext(ioDispatcher) {
        return@withContext when (val result = tripCollection.document(tripId).get().await()){
            is Result.Success -> {
                val trip = result.data.toObject(TripWithDataRemoteDB::class.java)
                if (trip != null){
                    when(val resultWatchers = fetchTripWatchers(trip.watchersId)){
                        is Result.Success -> Result.Success(trip.convertForLocal(resultWatchers.data))
                        is Result.Error -> Result.Error(resultWatchers.exception)
                        is Result.Canceled -> Result.Canceled(resultWatchers.exception)
                    }
                } else Result.Success(null)
            }
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Fetch the list of [TripWithData] the user is watching
     *
     * @param userId ID of the user
     * @return
     */
    override suspend fun fetchTripUserWatching(userId: String): Result<List<TripWithData>> = withContext(ioDispatcher) {
        return@withContext when(val result =
            tripCollection.whereArrayContains("watchersId", userId).get().await()){
            is Result.Success -> {
                val tripsWithData = mutableListOf<TripWithData>()
                var error = false
                coroutineScope {
                    result.data.toObjects(TripWithDataRemoteDB::class.java).forEach { trip ->
                        launch {
                            when (val resultWatchers = fetchTripWatchers(trip.watchersId)) {
                                is Result.Success -> tripsWithData.add(
                                    trip.convertForLocal(
                                        resultWatchers.data
                                    )
                                )
                                else -> error = true
                            }
                        }
                    }
                }
                when (error){
                    true -> Result.Error(Exception("Error while fetching the trip's watchers"))
                    false -> Result.Success(tripsWithData)
                }
            }
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }

    /**
     * Fetch the information of a owner of a trip
     *
     * @param ownerId ID of the user
     * @return [Result] of the operation with a [User] object
     */
    override suspend fun fetchTripOwner(ownerId: String): Result<User> = withContext(ioDispatcher) {
        return@withContext when (val task = userCollection.document(ownerId).get().await()){
            is Result.Success -> {
                if (task.data != null){
                    Result.Success(task.data.toObject(User::class.java)!!)
                } else {
                    Result.Error(Exception("no user found with id $ownerId ${task.data}"))
                }

            }
            is Result.Error -> Result.Error(task.exception)
            is Result.Canceled -> Result.Canceled(task.exception)
        }
    }

    /**
     * Update a [TripWithData] status
     *
     * @param trip trip to update
     * @return [Result] of the operation
     */
    override suspend fun updateTripStatus(trip: TripWithData): Result<Void?> = withContext(ioDispatcher) {
        return@withContext tripCollection.document(trip.trip.id).update(
            "trip.status", trip.trip.status,
            "trip.active", trip.trip.active
        ).await()
    }

    /**
     * TODODelete a [TripWithData] from the database
     *
     * @param trip trip to delete
     * @return [Result] of the operation
     */
    override suspend fun deleteTrip(trip: TripWithData): Result<Void?> = withContext(ioDispatcher) {
        return@withContext tripCollection.document(trip.trip.id).delete().await()
    }
}