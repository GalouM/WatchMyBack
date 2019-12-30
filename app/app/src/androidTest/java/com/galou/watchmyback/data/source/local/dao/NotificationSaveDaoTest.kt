package com.galou.watchmyback.data.source.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.entity.NotificationEmittedSaver
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * @author galou
 * 2019-12-28
 */
@RunWith(AndroidJUnit4::class)
class NotificationSaveDaoTest{

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: WatchMyBackDatabase
    private lateinit var userDao: UserDao
    private lateinit var tripDao: TripDao
    private lateinit var checkListDao: CheckListDao
    private lateinit var notificationSaverDao: NotificationSaverDao

    private val notificationSaver = NotificationEmittedSaver(
        tripId = tripWithData1.trip.id,
        userId = mainUser.id,
        lateNotificationEmitted = false,
        backSafeNotificationEmitted = false
    )

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WatchMyBackDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userDao = db.userDao()
        tripDao = db.tripDao()
        checkListDao = db.checkListDao()
        notificationSaverDao = db.notificationSaverDao()

        runBlocking {
            userDao.createUser(mainUser)
            checkListDao.createCheckListAndItems(
                checkList1,
                itemList1
            )
            checkListDao.createCheckListAndItems(
                checkList2,
                itemList2
            )

            tripDao.createTripAndData(
                tripWithData1,
                itemList1
            )
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        db.close()
    }

    @Test
    fun createAndFetchNotificationSaver() = runBlocking {
        notificationSaverDao.createNotificationSaver(notificationSaver)

        val fromDB = notificationSaverDao.fetchNotificationForTrip(mainUser.id, tripWithData1.trip.id)
        assertThat(fromDB, `is`(notificationSaver))
    }

    @Test
    fun updateAndFetchNotificationSaver() = runBlocking {
        notificationSaverDao.createNotificationSaver(notificationSaver)

        notificationSaver.backSafeNotificationEmitted = true
        notificationSaverDao.updateNotificationSaver(notificationSaver)
        val fromDB = notificationSaverDao.fetchNotificationForTrip(mainUser.id, tripWithData1.trip.id)
        assertThat(fromDB?.backSafeNotificationEmitted, `is` (true))

    }

}