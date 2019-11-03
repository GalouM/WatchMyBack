package com.galou.watchmyback.settings

import android.os.Build
import android.widget.RadioButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.galou.watchmyback.R
import com.galou.watchmyback.WatchMyBackApplication
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.data.entity.User
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
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by galou on 2019-10-31
 */
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
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
    fun modifyUnitsystem_saveData(){
        val context = ApplicationProvider.getApplicationContext<WatchMyBackApplication>()
        val buttonMetric = RadioButton(context)
        buttonMetric.id = R.id.settings_view_unit_system_unit_metric
        viewModel.updateUnitSytem(buttonMetric)

        val dataSavedMetric = LiveDataTestUtil.getValue(viewModel.dataSaved)
        assertThat(dataSavedMetric.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun modifyTimeDisplay_saveData(){
        val context = ApplicationProvider.getApplicationContext<WatchMyBackApplication>()
        val button12h = RadioButton(context)
        button12h.id = R.id.settings_view_unit_system_time_12
        viewModel.updateTimeDisplay(button12h)

        val dataSaved12 = LiveDataTestUtil.getValue(viewModel.dataSaved)
        assertThat(dataSaved12.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun deleteData_emitActionToView(){
        viewModel.deleteUserData()
        val deleteEvent = LiveDataTestUtil.getValue(viewModel.dataDeleted)
        assertThat(deleteEvent.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun errorDeletion_showMessage(){
        viewModel.errorDeletion()
        assertSnackBarMessage(viewModel.snackbarMessage,
            R.string.error_deletion
        )
    }



}