package com.galou.watchmyback.detailsPoint

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.data.entity.PointTrip
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.TypePoint
import com.galou.watchmyback.data.repository.FakeTripRepositoryImpl
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.testHelpers.LiveDataTestUtil
import com.galou.watchmyback.testHelpers.preferencesTest
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

/**
 * @author galou
 * 2019-12-16
 */
class DetailsPointViewModelTest {

    private lateinit var viewModel: DetailsPointViewModel
    private lateinit var userRepository: FakeUserRepositoryImpl
    private lateinit var tripRepository: FakeTripRepositoryImpl

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setup(){
        Dispatchers.setMain(mainThreadSurrogate)
        userRepository = FakeUserRepositoryImpl()
        userRepository.userPreferences.value = preferencesTest
        tripRepository = FakeTripRepositoryImpl()
        tripRepository.pointSelected = PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE))
        viewModel = DetailsPointViewModel(tripRepository, userRepository)
    }

    @ExperimentalCoroutinesApi
    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    fun init_emitPointAndUserData_scheduledPoint(){
        assertThat(LiveDataTestUtil.getValue(viewModel.pointTripLD)).isEqualTo(tripRepository.pointSelected)
        assertThat(LiveDataTestUtil.getValue(viewModel.userPreferencesLD)).isEqualTo(userRepository.userPreferences.value)
        assertThat(LiveDataTestUtil.getValue(viewModel.isScheduledPoint)).isTrue()
    }

    @Test
    fun init_checkedUpPoint_emitIsNotScheduled(){
        tripRepository.pointSelected = PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.CHECKED_UP))
        viewModel = DetailsPointViewModel(tripRepository, userRepository)
        assertThat(LiveDataTestUtil.getValue(viewModel.isScheduledPoint)).isFalse()

    }
}