package com.galou.watchmyback.settings

import android.os.Build
import android.widget.RadioButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.galou.watchmyback.R
import com.galou.watchmyback.WatchMyBackApplication
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.testHelpers.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.annotation.Config

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

    @Before
    fun setupViewModel(){
        Dispatchers.setMain(mainThreadSurrogate)
        userRepository = FakeUserRepositoryImpl()
        fakeUser = generateTestUser(TEST_UID)
        userRepository.currentUser.value = fakeUser
        userRepository.userPreferences.value = preferencesTest
        viewModel = SettingsViewModel(userRepository)

    }

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



}