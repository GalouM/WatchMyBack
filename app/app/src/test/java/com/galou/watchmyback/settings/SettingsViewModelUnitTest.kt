package com.galou.watchmyback.settings

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.Configuration
import androidx.work.impl.utils.SynchronousExecutor
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.testHelpers.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

/**
 * Created by galou on 2019-10-31
 */
class SettingsViewModelUnitTest : KoinTest {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var userRepository: FakeUserRepositoryImpl
    private lateinit var fakeUser: User

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setupViewModel(){
        Dispatchers.setMain(mainThreadSurrogate)

        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        userRepository = FakeUserRepositoryImpl()
        fakeUser = generateTestUser(TEST_UID)
        userRepository.currentUser.value = fakeUser
        userRepository.userPreferences.value = preferencesTest
        viewModel = SettingsViewModel(userRepository)

    }

    @ExperimentalCoroutinesApi
    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    @Throws(Exception::class)
    fun init_emitUserPreferences(){
        assertThat(LiveDataTestUtil.getValue(viewModel.preferencesLD)).isEqualTo(preferencesTest)
    }

    @Test
    fun changeData_updatePreferences(){
        viewModel.updateUserPreferences()
        val dataSaved = LiveDataTestUtil.getValue(viewModel.dataSaved)
        assertThat(dataSaved.getContentIfNotHandled()).isNotNull()
    }

    @Test
    @Throws(Exception::class)
    fun modifyUnitsystem_saveData(){
        viewModel.updateUnitSytem(R.id.settings_view_unit_system_unit_metric)

        val dataSavedMetric = LiveDataTestUtil.getValue(viewModel.dataSaved)
        assertThat(dataSavedMetric.getContentIfNotHandled()).isNotNull()
    }

    @Test
    @Throws(Exception::class)
    fun modifyTimeDisplay_saveData(){
        viewModel.updateTimeDisplay(R.id.settings_view_unit_system_time_12)

        val dataSaved12 = LiveDataTestUtil.getValue(viewModel.dataSaved)
        assertThat(dataSaved12.getContentIfNotHandled()).isNotNull()
    }

    @Test
    @Throws(Exception::class)
    fun deleteData_emitActionToView(){
        viewModel.deleteUserData()
        val deleteEvent = LiveDataTestUtil.getValue(viewModel.dataDeleted)
        assertThat(deleteEvent.getContentIfNotHandled()).isNotNull()
    }

    @Test
    @Throws(Exception::class)
    fun errorDeletion_showMessage(){
        viewModel.errorDeletion()
        assertSnackBarMessage(viewModel.snackbarMessage,
            R.string.error_deletion
        )
    }

    @Test
    @Throws(Exception::class)
    fun enableNotificationLate_modifyPrefs(){
        viewModel.clickNotificationLate(true)
        assertThat(userRepository.userPreferences.value?.notificationLate).isTrue()
        val value = LiveDataTestUtil.getValue(viewModel.enableLateNotificationLD)
        assertThat(value.getContentIfNotHandled()).contains(userRepository.currentUser.value?.id)
    }

    @Test
    @Throws(Exception::class)
    fun disableNotificationLate_modifyPrefs(){
        viewModel.clickNotificationLate(false)
        assertThat(userRepository.userPreferences.value?.notificationLate).isFalse()
        val value = LiveDataTestUtil.getValue(viewModel.disableLateNotificationLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()

    }

    @Test
    @Throws(Exception::class)
    fun enableNotificationBackHome_modifyPrefs(){
        viewModel.clickNotificationBackHome(true)
        assertThat(userRepository.userPreferences.value?.notificationBackSafe).isTrue()
        val value = LiveDataTestUtil.getValue(viewModel.enableBackHomeNotificationLD)
        assertThat(value.getContentIfNotHandled()).contains(userRepository.currentUser.value?.id)
    }

    @Test
    @Throws(Exception::class)
    fun disableNotificationBackHome_modifyPrefs(){
        viewModel.clickNotificationBackHome(false)
        assertThat(userRepository.userPreferences.value?.notificationBackSafe).isFalse()
        val value = LiveDataTestUtil.getValue(viewModel.disableBackHomeNotificationLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()

    }



}