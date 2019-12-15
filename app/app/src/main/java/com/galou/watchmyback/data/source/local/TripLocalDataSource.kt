package com.galou.watchmyback.data.source.local

import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.source.TripDataSource
import com.galou.watchmyback.data.source.local.dao.TripDao
import com.galou.watchmyback.utils.Result

/**
 * @author galou
 * 2019-12-08
 */
class TripLocalDataSource(
    private val tripDao: TripDao
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
}