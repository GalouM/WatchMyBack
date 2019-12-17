package com.galou.watchmyback.tripMapView

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TypePoint
import com.galou.watchmyback.data.repository.FakeTripRepositoryImpl
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.testHelpers.LiveDataTestUtil
import com.galou.watchmyback.testHelpers.assertSnackBarMessage
import com.galou.watchmyback.testHelpers.mainUser
import com.galou.watchmyback.testHelpers.tripWithData
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
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
 * 2019-11-30
 */
class TripMapViewModelTest {

    private lateinit var viewModel: TripMapViewModel
    private lateinit var tripRepository: FakeTripRepositoryImpl
    private lateinit var userRepository: FakeUserRepositoryImpl

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        Dispatchers.setMain(mainThreadSurrogate)
        tripRepository = FakeTripRepositoryImpl()
        userRepository = FakeUserRepositoryImpl()
        userRepository.currentUser.value = mainUser
        viewModel = TripMapViewModel(tripRepository, userRepository)
    }

    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    fun clickStartTrip_openAddTripActivity(){
        viewModel.clickStartNewTrip()
        val value = LiveDataTestUtil.getValue(viewModel.openAddTripActivity)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickCenterCameraOnUser_centerCameraOnUser(){
        viewModel.centerCameraOnUser()
        val value = LiveDataTestUtil.getValue(viewModel.centerCameraUserLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun gpsNoeAvailable_showMessage(){
        viewModel.gpsNotAvailable()
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.turn_on_gps)
    }

    @Test
    fun fetchAndDisplayUserActiveTrip_showTripPoints(){
        viewModel.fetchAndDisplayUserActiveTrip()
        assertThat(LiveDataTestUtil.getValue(viewModel.startPointLD).toList()[0].first)
            .isEqualTo(tripWithData.points.find { it.pointTrip.typePoint == TypePoint.START }?.pointTrip?.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.endPointLD).toList()[0].first)
            .isEqualTo(tripWithData.points.find { it.pointTrip.typePoint == TypePoint.END }?.pointTrip?.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.schedulePointsLD).keys)
            .containsExactlyElementsIn(tripWithData.points
                .filter { it.pointTrip.typePoint == TypePoint.SCHEDULE_STAGE }
                .map { it.pointTrip.id })
        assertThat(LiveDataTestUtil.getValue(viewModel.checkedPointsLD).keys)
            .containsExactlyElementsIn(tripWithData.points
                .filter { it.pointTrip.typePoint == TypePoint.CHECKED_UP }
                .map { it.pointTrip.id })
    }

    @Test
    fun fetchAndDisplayUserActiveTrip_showTripInfo(){
        viewModel.fetchAndDisplayUserActiveTrip()
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD)).isEqualTo(tripWithData.trip)

    }

    @Test
    fun clickPointMap_selectPointAndShowDetails(){
        viewModel.fetchAndDisplayUserActiveTrip()
        LiveDataTestUtil.getValue(viewModel.startPointLD) //wait fro view model to fetch the current trip
        viewModel.clickPointTrip(tripWithData.points[0].pointTrip.id)
        assertThat(tripRepository.pointSelected).isEqualTo(tripWithData.points[0])
        val value = LiveDataTestUtil.getValue(viewModel.showPointDetailsLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }
}