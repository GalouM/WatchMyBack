package com.galou.watchmyback.sources

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.source.local.UserLocalDataSource
import com.galou.watchmyback.data.source.local.dao.UserDao
import com.galou.watchmyback.data.source.local.dao.UserPreferencesDao
import com.galou.watchmyback.database.mainUser
import com.galou.watchmyback.utils.Result
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
 * 2019-11-01
 */
@RunWith(AndroidJUnit4::class)
class UserLocalSourceTests{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: WatchMyBackDatabase
    private lateinit var localSource: UserLocalDataSource
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
        localSource = UserLocalDataSource(userDao, preferencesDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createAndFetchUser_createdInDBAndEmitSuccess() = runBlocking {
        // create user and default prefs
        val creationTask = localSource.createUser(mainUser)

        //check creation operation was successful
        val creationResult = creationTask is Result.Success
        assertThat(creationResult, `is` (true))

        // fetch
        val fetchTask = localSource.fetchUser(mainUser.id)

        //check fetch operation was successfull
        val fetchResult = fetchTask is Result.Success
        assertThat(fetchResult, `is` (true))

        // check data are fetched and exist in DB
        val fetchedData = (fetchTask as Result.Success).data
        assertThat(mainUser ,`is` (fetchedData?.user))
        assertThat(mainUser.id, `is` (fetchedData?.preferences?.id))
    }

    @Test
    @Throws(Exception::class)
    fun updateUserInformation() = runBlocking {
        val newPhoneNumber = "5559874637"
        mainUser.phoneNumber = newPhoneNumber
    }
}