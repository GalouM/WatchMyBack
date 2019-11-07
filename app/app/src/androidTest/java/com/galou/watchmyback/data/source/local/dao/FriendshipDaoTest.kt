package com.galou.watchmyback.data.source.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.hasItem
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
class FriendshipDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: WatchMyBackDatabase
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

        runBlocking {
            userDao.createUser(mainUser)
            userDao.createUser(firstFriend)
            userDao.createUser(secondFriend)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun addAndRemoveAndGetFriend() = runBlocking {
        friendDao.createFriendship(friendship1)
        friendDao.createFriendship(friendship2)

        val friends = friendDao.getFriendsUser(mainUser.id)

        assertEquals(friends.size, 2)
        assertThat(friends, hasItem(firstFriend))

        friendDao.removeFriend(secondFriend.id, mainUser.id)

        val friendsRemoved = friendDao.getFriendsUser(mainUser.id)
        assertEquals(friendsRemoved.size, 1)

    }


}