package com.galou.watchmyback.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.source.local.dao.*
import com.galou.watchmyback.utils.Result
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
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

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WatchMyBackDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userDao = db.userDao()
        tripDao = db.tripDao()
        localSource = TripLocalDataSource(tripDao)

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
}