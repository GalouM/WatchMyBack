package com.galou.watchmyback.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.database.WatchMyBackDatabase
import com.galou.watchmyback.data.database.dao.FriendDao
import com.galou.watchmyback.data.database.dao.UserDao
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Created by galou on 2019-10-22
 */
@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: WatchMyBackDatabase
    private lateinit var userDao: UserDao

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WatchMyBackDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createAndGetUser() = runBlocking {
        userDao.createUser(mainUser)
        val userFromDB = userDao.getUser(mainUser.id)
        assertEquals(mainUser.id, userFromDB[0].id)
    }

    @Test
    @Throws(Exception::class)
    fun deleteUser() = runBlocking {
        userDao.createUser(mainUser)
        val userFromDB = userDao.getUser(mainUser.id)
        assertEquals(userFromDB.size, 1)
        userDao.deleteUser(mainUser.id)
        val userDeleted = userDao.getUser(mainUser.id)
        assertEquals(userDeleted.size, 0)
    }


}