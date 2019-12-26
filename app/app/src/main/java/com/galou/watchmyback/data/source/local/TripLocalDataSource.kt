package com.galou.watchmyback.data.source.local

import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.TripDataSource
import com.galou.watchmyback.data.source.local.dao.TripDao
import com.galou.watchmyback.data.source.local.dao.UserDao
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-12-08
 */
class TripLocalDataSource(
    private val tripDao: TripDao,
    private val userDao: UserDao
) : TripDataSource {

    /**
     * Create a [TripWithData] in the database and update the item of a checklist
     *
     * @param trip [TripWithData] to create
     * @param checkList checklist assigned to the trip
     * @return [Result] of the operation
     *
     * @see TripDao.createTripAndData
     */
    override suspend fun createTrip(
        trip: TripWithData,
        checkList: CheckListWithItems?
    ): Result<Void?> {
        return try {
            tripDao.createTripAndData(trip, checkList?.items)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Delete all the active trip of a user in the database
     *
     * @param userId Id of the user
     * @return [Result] of the operation
     *
     * @see TripDao.deleteActiveTrips
     */
    override suspend fun deleteActiveTrip(userId: String): Result<Void?> {
        return try {
            tripDao.deleteActiveTrips(userId)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Fetch the user active trip from the database
     *
     * @param userId ID of the user
     * @return [Result] of the operation with a [TripWithData]
     */
    override suspend fun fetchActiveTrip(userId: String): Result<TripWithData?> {
        return try {
            Result.Success(tripDao.getUserActiveTrip(userId))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Fetch the [TripWithData] with the corresponding ID
     *
     * @param tripId ID of the trip
     * @return [Result] of the operation with a [TripWithData]
     */
    override suspend fun fetchTrip(tripId: String): Result<TripWithData?> {
        return try {
            Result.Success(tripDao.getTrip(tripId))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Fetch the list of [TripWithData] the user is watching
     *
     * @param userId ID of the user
     * @return
     */
    override suspend fun fetchTripUserWatching(userId: String): Result<List<TripWithData>> {
        return try {
            Result.Success(tripDao.getTripsUserWatching(userId))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Fetch the information of a owner of a trip
     *
     * @param ownerId ID of the user
     * @return [Result] of the operation with a [User] object
     */
    override suspend fun fetchTripOwner(ownerId: String): Result<User> {
        return try {
            userDao.getUser(ownerId)?.let {
                Result.Success(it)
            }
            Result.Error(Exception("No user with ID $ownerId"))

        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Update a [TripWithData] status
     *
     * @param trip trip to update
     * @return [Result] of the operation
     */
    override suspend fun updateTripStatus(trip: TripWithData): Result<Void?> {
        return try {
            tripDao.updateTrip(trip.trip)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * TODODelete a [Trip] from the database
     *
     * @param trip trip to delete
     * @return [Result] of the operation
     */
    override suspend fun deleteTrip(trip: TripWithData): Result<Void?> {
        return try {
            tripDao.deleteTrip(trip.trip)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}