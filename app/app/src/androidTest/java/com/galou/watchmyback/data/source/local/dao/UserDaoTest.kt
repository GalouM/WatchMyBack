package com.galou.watchmyback.data.source.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
    private lateinit var preferencesDao: UserPreferencesDao

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WatchMyBackDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userDao = db.userDao()
        preferencesDao = db.userPreferencesDao()
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
        assertNotNull(userFromDB)
        assertEquals(mainUser.id, userFromDB?.id)
    }

    @Test
    @Throws(Exception::class)
    fun deleteUser() = runBlocking {
        userDao.createUser(mainUser)
        val userFromDB = userDao.getUser(mainUser.id)
        assertNotNull(userFromDB)
        userDao.deleteUser(mainUser)
        val userDeleted = userDao.getUser(mainUser.id)
        assertNull(userDeleted)
    }

    @Test
    @Throws(Exception::class)
    fun createUserAndPreferences() = runBlocking{
        userDao.createOrUpdateUserWithData(true, mainUser, mainUserPreferences)
        val preferencesFromDB = preferencesDao.getUserPreferences(mainUser.id)
        assertNotNull(preferencesFromDB)
        val userWithPreferences = userDao.getUserWithPreferences(mainUser.id)
        assertEquals(userWithPreferences?.user?.id, mainUser.id)
        assertEquals(userWithPreferences?.preferences?.id, mainUser.id)
    }

    @Test
    @Throws(Exception::class)
    fun updatePreferencesUser() = runBlocking {
        userDao.createOrUpdateUserWithData(false, mainUser, mainUserPreferences)
        val newEmergencyNumber = "911"
        mainUserPreferences.emergencyNumber = newEmergencyNumber
        preferencesDao.updateUserPreferences(mainUserPreferences)
        val preferencesFromDB = preferencesDao.getUserPreferences(mainUser.id)
        assertNotNull(preferencesFromDB)
        assertEquals(preferencesFromDB?.emergencyNumber, newEmergencyNumber)
    }

    @Test
    @Throws(Exception::class)
    fun fetchAllTheUser() = runBlocking{
        userDao.createUser(mainUser)
        userDao.createUser(firstFriend)
        userDao.createUser(secondFriend)
        val users = userDao.getAllUsers()
        assertThat(users, `is`(notNullValue()))
        assertThat(users, hasItem(mainUser))
        assertThat(users, hasItem(firstFriend))
        assertThat(users, hasItem(secondFriend))
        assertThat(users.size, equalTo(3))
    }

    @Test
    @Throws(Exception::class)
    fun fetchUserByUsername() = runBlocking{
        userDao.createUser(mainUser)
        userDao.createUser(firstFriend)
        userDao.createUser(secondFriend)
        val users = userDao.getUsersFromUsername("user")
        assertThat(users, `is`(notNullValue()))
        assertThat(users, hasItem(mainUser))
        assertThat(users.size, equalTo(1))
    }

    @Test
    @Throws(Exception::class)
    fun fetchUserByEmailAddress() = runBlocking {
        userDao.createUser(mainUser)
        userDao.createUser(firstFriend)
        userDao.createUser(secondFriend)
        val users = userDao.getUsersFromEmail("friend")
        assertThat(users, `is`(notNullValue()))
        assertThat(users, hasItem(firstFriend))
        assertThat(users, hasItem(secondFriend))
        assertThat(users.size, equalTo(2))

    }


}