package com.galou.watchmyback.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.entity.UserWithPreferences
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.source.local.dao.*
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
@MediumTest
@RunWith(AndroidJUnit4::class)
class UserLocalSourceTests{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: WatchMyBackDatabase
    private lateinit var localSource: UserLocalDataSource
    private lateinit var userDao: UserDao
    private lateinit var preferencesDao: UserPreferencesDao
    private lateinit var friendDao: FriendDao

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WatchMyBackDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userDao = db.userDao()
        preferencesDao = db.userPreferencesDao()
        friendDao = db.friendDao()
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
        assertThat(mainUser,`is` (fetchedData?.user))
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
        val taskData = (task as Result.Success).data
        assertThat(taskData?.user, `is` (mainUser))
        assertThat(taskData?.preferences, `is` (notNullValue()))

    }

    @Test
    @Throws(Exception::class)
    fun userExistLocallyButDifferentThanRemote_updateUserLocallyAndEmitSuccess() = runBlocking {
        localSource.createUser(mainUser)
        val remoteUser = mainUser.copy()
        val newPhotoUrl = "http://mynewPicturlUrl"
        remoteUser.pictureUrl =  newPhotoUrl
        val localUser = UserWithPreferences(user = mainUser, preferences = UserPreferences(id = mainUser.id))
        val task = localSource.updateOrCreateUser(remoteUser, localUser)

        //check operation was successful
        val taskResult = task is Result.Success
        assertThat(taskResult, `is` (true))

        // check user now exist in local database
        val taskData = (task as Result.Success).data
        assertThat(taskData?.user, `is` (remoteUser))
        assertThat(taskData?.preferences, `is` (notNullValue()))

    }

    @Test
    @Throws(Exception::class)
    fun localAndRemoteUserAreTheSame_emitSuccess() = runBlocking {
        localSource.createUser(mainUser)
        val localUser = UserWithPreferences(user = mainUser, preferences = UserPreferences(id = mainUser.id))
        val task = localSource.updateOrCreateUser(mainUser, localUser)
        //check operation was successful
        val taskResult = task is Result.Success
        assertThat(taskResult, `is` (true))
        val taskData = (task as Result.Success).data
        assertThat(taskData, `is` (localUser))

    }

    @Test
    @Throws(Exception::class)
    fun fetchUserWithFriends_emitSuccessAndCreateFriendsInDB() = runBlocking {
        // add friend to user
        mainUser.friendsId.add(firstFriend.id)
        mainUser.friendsId.add(secondFriend.id)
        // create user locally
        val task = localSource.updateOrCreateUser(mainUser, null, firstFriend, secondFriend)
        //check user was created
        val taskResult = task is Result.Success
        assertThat(taskResult, `is` (true))
        val taskData = (task as Result.Success).data
        assertThat(taskData?.user, `is` (mainUser))
        // check friends were created
        val userFriends = friendDao.getFriendsUser(mainUser.id)
        assertThat(userFriends, hasItem(firstFriend))
        assertThat(userFriends, hasItem(secondFriend))
        assertThat(userFriends.size, `is` (2))

    }

    @Test
    @Throws(Exception::class)
    fun fetchAllUser_emitSuccessAndListAllUsers() = runBlocking {
        localSource.createUser(mainUser)
        localSource.createUser(firstFriend)
        localSource.createUser(secondFriend)
        val task = localSource.fetchAllUsers()
        val taskResult = task is Result.Success
        assertThat(taskResult, `is` (true))
        val taskData = (task as Result.Success).data
        assertThat(taskData, `is`(notNullValue()))
        assertThat(taskData, hasItem(mainUser))
        assertThat(taskData, hasItem(firstFriend))
        assertThat(taskData, hasItem(secondFriend))
        assertThat(taskData.size, equalTo(3))

    }

    @Test
    @Throws(Exception::class)
    fun fetchUserByUsername_emitSuccessAndListUser() = runBlocking {
        localSource.createUser(mainUser)
        localSource.createUser(firstFriend)
        localSource.createUser(secondFriend)
        val task = localSource.fetchUserByUsername("user")
        val taskResult = task is Result.Success
        assertThat(taskResult, `is` (true))
        val taskData = (task as Result.Success).data
        assertThat(taskData, `is`(notNullValue()))
        assertThat(taskData, hasItem(mainUser))
        assertThat(taskData.size, equalTo(1))

    }

    @Test
    @Throws(Exception::class)
    fun fetchUserByEmail_emitSuccessAndListUser() = runBlocking {
        localSource.createUser(mainUser)
        localSource.createUser(firstFriend)
        localSource.createUser(secondFriend)
        val task = localSource.fetchUserByEmailAddress("friend")
        val taskResult = task is Result.Success
        assertThat(taskResult, `is` (true))
        val taskData = (task as Result.Success).data
        assertThat(taskData, `is`(notNullValue()))
        assertThat(taskData, hasItem(firstFriend))
        assertThat(taskData, hasItem(secondFriend))
        assertThat(taskData.size, equalTo(2))

    }


}