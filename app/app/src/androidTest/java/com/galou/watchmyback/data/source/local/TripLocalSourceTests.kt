package com.galou.watchmyback.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.TripStatus
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.source.local.dao.*
import com.galou.watchmyback.utils.Result
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * @author galou
 * 2019-12-13
 */

@MediumTest
@RunWith(AndroidJUnit4::class)
class TripLocalSourceTests {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: WatchMyBackDatabase
    private lateinit var localSource: TripLocalDataSource
    private lateinit var tripDao: TripDao
    private lateinit var userDao: UserDao
    private lateinit var pointTripDao: PointTripDao

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WatchMyBackDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userDao = db.userDao()
        tripDao = db.tripDao()
        pointTripDao = db.pointTripDao()
        localSource = TripLocalDataSource(tripDao, userDao, pointTripDao)

        runBlocking {
            userDao.createUser(mainUser)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createTrip() = runBlocking {
        val task = localSource.createTrip(tripWithData1, checkListWithItem1)
        val result = task is Result.Success
        assertThat(result, `is`(true))

        val trip = tripDao.getUserActiveTrip(tripWithData1.trip.userId)

        assertThat(trip, `is` (tripWithData1))
    }

    @Test
    @Throws(Exception::class)
    fun deleteActiveTrip_deleteAllActiveTrip() = runBlocking {
        tripDao.createTrip(trip1)
        tripDao.createTrip(trip2)

        val task = localSource.deleteActiveTrip(trip1.userId)
        val result = task is Result.Success
        assertThat(result, `is`(true))

        val trip = tripDao.getUserActiveTrip(trip1.userId)

        assertThat(trip, `is` (nullValue()))

    }

    @Test
    @Throws(Exception::class)
    fun fetchUserActiveTrip_returnUserActiveTrip() = runBlocking {
        tripDao.createTripAndData(tripWithData1, itemList1)
        val task = localSource.fetchActiveTrip(trip1.userId)
        val result = task is Result.Success
        assertThat(result, `is`(true))
        val trip = (task as Result.Success).data
        assertThat(trip, `is` (tripWithData1))
    }

    @Test
    @Throws(Exception::class)
    fun fetchTripByID_returnTripWithCorrectID() = runBlocking {
        tripDao.createTripAndData(tripWithData1, itemList1)
        val task = localSource.fetchTrip(tripWithData1.trip.id)
        val result = task is Result.Success
        assertThat(result, `is`(true))
        val trip = (task as Result.Success).data
        assertThat(trip?.trip?.id, `is` (tripWithData1.trip.id))

    }

    @Test
    @Throws(Exception::class)
    fun fetchTripWatching_returnTripUserWatching() = runBlocking {
        tripDao.createTripAndData(tripWithData1, itemList1)
        tripDao.createTripAndData(tripWithData2, itemList2)
        val task = localSource.fetchTripUserWatching(secondFriend.id)
        val result = task is Result.Success
        assertThat(result, `is`(true))
        val trips = (task as Result.Success).data
        trips.forEach {
            assertThat(it.watchers, hasItem(secondFriend))
        }

    }

    @Test
    @Throws(Exception::class)
    fun fetchTripOwner_returnTripOwnerInfo() = runBlocking {
        tripDao.createTripAndData(tripWithData1, itemList1)
        val task = localSource.fetchTripOwner(tripWithData1.trip.userId)
        val result = task is Result.Success
        assertThat(result, `is`(true))
        val user = (task as Result.Success).data
        assertThat(user.id, `is` (tripWithData1.trip.userId))

    }

    @Test
    @Throws(Exception::class)
    fun updateTripStatus_updateStatus() = runBlocking {
        tripDao.createTripAndData(tripWithData1, itemList1)
        tripWithData1.trip.status = TripStatus.BACK_SAFE
        val task = localSource.updateTripStatus(tripWithData1)
        val result = task is Result.Success
        assertThat(result, `is`(true))
        val trip = tripDao.getTrip(tripWithData1.trip.id)
        assertThat(trip?.trip?.status, `is`(TripStatus.BACK_SAFE))
    }

    @Test
    @Throws(Exception::class)
    fun deleteTrip_deleteTripFromDB() = runBlocking {
        tripDao.createTripAndData(tripWithData1, itemList1)
        val task = localSource.deleteTrip(tripWithData1)
        val result = task is Result.Success
        assertThat(result, `is`(true))
        val trip = tripDao.getTrip(tripWithData1.trip.id)
        assertThat(trip, `is` (nullValue()))
    }

    @Test
    @Throws(Exception::class)
    fun updateListPoints_updateInDB() = runBlocking {
        tripDao.createTripAndData(tripWithData1, itemList1)
        val newPoint = PointTripWithData()
        tripWithData1.points.add(newPoint)
        val task = localSource.updateTripPoints(tripWithData1)
        val result = task is Result.Success
        assertThat(result, `is`(true))
        val trip = tripDao.getTrip(tripWithData1.trip.id)
        assertThat(trip?.points, hasItem(newPoint))
    }
}