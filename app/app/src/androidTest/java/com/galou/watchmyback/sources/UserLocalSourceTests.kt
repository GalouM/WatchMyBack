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
    fun updateUserInformation_updatedInDBAndEmitSuccess() = runBlocking {
        localSource.createUser(mainUser)
        // update user
        val newPhoneNumber = "5559874637"
        mainUser.phoneNumber = newPhoneNumber
        val updateTask = localSource.updateUserInformation(mainUser)

        //check operation was successful
        val updateResult = updateTask is Result.Success
        assertThat(updateResult, `is` (true))

        // fetch user and check informations were updated
        val fetchTask = localSource.fetchUser(mainUser.id)
        val fetchedData = (fetchTask as Result.Success).data
        assertThat(mainUser.phoneNumber, `is` (fetchedData?.user?.phoneNumber))

    }

    @Test
    @Throws(Exception::class)
    fun updateUserPreferences_updatedInDBAndEmitSuccess() = runBlocking {
        localSource.createUser(mainUser)
        // fetch to get preferences
        val fetchTask = localSource.fetchUser(mainUser.id)
        val preferences = (fetchTask as Result.Success).data?.preferences!!

        // update preferences
        val newEmergencyNumber = "112"
        preferences.emergencyNumber = newEmergencyNumber
        val updateTask = localSource.updateUserPreference(preferences)

        //check operation was successful
        val updateResult = updateTask is Result.Success
        assertThat(updateResult, `is` (true))

        // fetch preferences and check information were updated

        val fetchTaskAfterUpdate = localSource.fetchUser(mainUser.id)
        val fetchedPreferences = (fetchTaskAfterUpdate as Result.Success).data?.preferences
        assertThat(fetchedPreferences?.emergencyNumber, `is` (newEmergencyNumber))

    }

    @Test
    @Throws(Exception::class)
    fun deleteUser_deleteUserInDBAndEmitSuccess() = runBlocking {
        localSource.createUser(mainUser)
        val deleteTask = localSource.deleteUser(mainUser)

        //check operation was successful
        val deleteResult = deleteTask is Result.Success
        assertThat(deleteResult, `is` (true))

        //check user and preferences were deleted
        val userFetched = userDao.getUser(mainUser.id)
        assertThat(userFetched, `is`(nullValue()))
        val preferencesFetched = preferencesDao.getUserPreferences(mainUser.id)
        assertThat(preferencesFetched, `is`(nullValue()))
    }

    @Test
    @Throws(Exception::class)
    fun userDoesntExistLocally_createUserInDBAndEmitSuccess() = runBlocking {
        val task = localSource.updateOrCreateUser(mainUser, null)

        //check operation was successful
        val taskResult = task is Result.Success
        assertThat(taskResult, `is` (true))

        // check user now exist in local database
        val userFromDB =  userDao.getUser(mainUser.id)
        assertThat(userFromDB, `is` (mainUser))
        val preferenceFromDB = preferencesDao.getUserPreferences(mainUser.id)
        assertThat(preferenceFromDB, `is` (notNullValue()))
        
    }


}