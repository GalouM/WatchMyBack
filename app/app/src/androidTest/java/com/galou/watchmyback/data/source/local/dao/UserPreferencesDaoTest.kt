package com.galou.watchmyback.data.source.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Created by galou on 2019-10-30
 */
@RunWith(AndroidJUnit4::class)
class UserPreferencesDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: WatchMyBackDatabase
    private lateinit var preferencesDao: UserPreferencesDao

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WatchMyBackDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        preferencesDao = db.userPreferencesDao()

        runBlocking {preferencesDao.createUserPreferences(mainUserPreferences)  }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createUserPreferences() = runBlocking{
        val preferencesFromDB = preferencesDao.getUserPreferences(mainUser.id)
        assertNotNull(preferencesFromDB)
    }

    @Test
    @Throws(Exception::class)
    fun updatePreferencesUser() = runBlocking {
        val newEmergencyNumber = "911"
        mainUserPreferences.emergencyNumber = newEmergencyNumber
        preferencesDao.updateUserPreferences(mainUserPreferences)
        val preferencesFromDB = preferencesDao.getUserPreferences(mainUser.id)
        org.junit.Assert.assertNotNull(preferencesFromDB)
        assertEquals(preferencesFromDB?.emergencyNumber, newEmergencyNumber)
    }

    @Test
    @Throws(Exception::class)
    fun deletePreferencesUser() = runBlocking {
        val preferencesFromDB = preferencesDao.getUserPreferences(mainUser.id)
        assertNotNull(preferencesFromDB)
        preferencesDao.deleteUserPreferences(mainUserPreferences)
        val preferencesFromDBDeletion = preferencesDao.getUserPreferences(mainUser.id)
        assertNull(preferencesFromDBDeletion)

    }
}