package com.galou.watchmyback.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.source.local.dao.*
import com.galou.watchmyback.data.entity.TypePoint
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Created by galou on 2019-10-22
 */
@RunWith(AndroidJUnit4::class)
class TripDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: WatchMyBackDatabase
    private lateinit var userDao: UserDao
    private lateinit var tripDao: TripDao
    private lateinit var checkListDao: CheckListDao
    private lateinit var watcherDao: WatcherDao
    private lateinit var pointTripDao: PointTripDao

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WatchMyBackDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userDao = db.userDao()
        tripDao = db.tripDao()
        checkListDao = db.checkListDao()
        watcherDao = db.watcherDao()
        pointTripDao = db.pointTripDao()

        runBlocking {
            userDao.createUser(mainUser)
            userDao.createUser(firstFriend)
            userDao.createUser(secondFriend)
            checkListDao.createCheckListAndItems(checkList1, itemList1)
            checkListDao.createCheckListAndItems(checkList2, itemList2)

            tripDao.createTripAndData(trip1, listPoint1, listLocationsTrip1, listWeatherTrip1, listWatcherTrip1, itemList1)
            tripDao.createTripAndData(trip2, listPoint2, listLocationTrip2, listWeatherTrip2, listWatcherTrip2, itemList2)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createAndGetTrip() = runBlocking {
        val tripFromDb = tripDao.getUserActiveTrip(mainUser.id)
        assertThat(tripFromDb, hasItem(trip1))

    }

    @Test
    @Throws(Exception::class)
    fun getTripUserWatching() = runBlocking {
        val tripWatching = watcherDao.getTripUserIsWatching(firstFriend.id)
        assertThat(tripWatching, hasItem(trip1))
        assertThat(tripWatching, hasSize(1))
    }

    @Test
    @Throws(Exception::class)
    fun getTripWatcher() = runBlocking {
        val tripWatcher = watcherDao.getWatcherTrip(trip1.id)
        assertThat(tripWatcher, hasSize(listWatcherTrip1.size))
        assertThat(tripWatcher, hasItem(watcher1Trip1))
    }

    @Test
    @Throws(Exception::class)
    fun getCheckListTrip() = runBlocking {
        val checkListTrip1FromDB = checkListDao.getCheckListWithItems(trip1.checkListId!!)
        assertThat(checkListTrip1FromDB, hasItem(checkList1))
        assertThat(checkListTrip1FromDB, hasSize(1))
    }

    @Test
    @Throws(Exception::class)
    fun getPointTrip() = runBlocking {
        val tripPointFromDB = pointTripDao.getAllPointsFromTripWithData(trip1.id)
        assertThat(tripPointFromDB, hasSize(listPoint1.size))
        val point2FromDB = tripPointFromDB.find { it.pointTrip.id == point2Trip1.id }
        assertEquals(point2FromDB!!.pointTrip, point2Trip1)
        val pointLocation = point2FromDB.location[0]
        assertEquals(pointLocation, location2Trip1)
        val pointWeather = point2FromDB.weatherData[0]
        assertEquals(pointWeather, weather2Trip1)
    }

    @Test
    @Throws(Exception::class)
    fun getPointTripByType() = runBlocking {
        val tripPointFromDB = pointTripDao.getAllPointsFromTripWithDataByType(trip1.id, TypePoint.START)
        assertThat(tripPointFromDB, hasSize(1))
        assertEquals(tripPointFromDB[0].pointTrip, point1Trip1)
    }


}