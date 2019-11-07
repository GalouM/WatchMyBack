package com.galou.watchmyback.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.entity.Friend
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.source.local.dao.*
import com.galou.watchmyback.utils.Result
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * @author galou
 * 2019-11-04
 */

@MediumTest
@RunWith(AndroidJUnit4::class)
class FriendLocalSourceTests{

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: WatchMyBackDatabase
    private lateinit var localSource: FriendLocalDataSource
    private lateinit var userDao: UserDao
    private lateinit var friendDao: FriendDao

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WatchMyBackDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userDao = db.userDao()
        friendDao = db.friendDao()
        localSource = FriendLocalDataSource(friendDao)

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
    fun addAndFetchFriendThenRemove_emitSuccess() = runBlocking {
        val creationTask = localSource.addFriend(mainUser, firstFriend, secondFriend)
        //check creation operation was successful
        val creationResult = creationTask is Result.Success
        assertThat(creationResult, `is`(true))

        // fetch
        val fetchTask = localSource.fetchUserFriend(mainUser)

        //check fetch operation was successful
        val fetchResult = fetchTask is Result.Success
        assertThat(fetchResult, `is`(true))

        // check data are fetched and exist in DB
        val fetchedData = (fetchTask as Result.Success).data
        assertThat(fetchedData.size, equalTo(2))
        assertThat(fetchedData, hasItem(firstFriend))
        assertThat(fetchedData, hasItem(secondFriend))

        val deleteTask = localSource.removeFriend(mainUser, firstFriend.id)

        //check delete operation was successful
        val deleteResult = deleteTask is Result.Success
        assertThat(deleteResult, `is`(true))

        // fetch
        val fetchDeletedTask = localSource.fetchUserFriend(mainUser)

        // check data are fetched and exist in DB
        val fetchedDeletedData = (fetchDeletedTask as Result.Success).data
        assertThat(fetchedDeletedData.size, equalTo(1))
    }

}