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
 * 2019-11-30
 */
class TripMapViewModelTest {

    private lateinit var viewModel: TripMapViewModel
    private lateinit var tripRepository: FakeTripRepositoryImpl
    private lateinit var userRepository: FakeUserRepositoryImpl

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setupViewModel(){
        Dispatchers.setMain(mainThreadSurrogate)
        tripRepository = FakeTripRepositoryImpl()
        userRepository = FakeUserRepositoryImpl()
        userRepository.currentUser.value = mainUser
        viewModel = TripMapViewModel(tripRepository, userRepository)
    }

    @ExperimentalCoroutinesApi
    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    fun clickStartTrip_openAddTripActivity(){
        viewModel.clickStartStop()
        val value = LiveDataTestUtil.getValue(viewModel.openAddTripActivity)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickStopTrip_showMessage(){
        viewModel.fetchAndDisplayUserActiveTrip()
        LiveDataTestUtil.getValue(viewModel.tripLD)
        viewModel.clickStartStop()
        val value = LiveDataTestUtil.getValue(viewModel.openAddTripActivity)
        assertThat(value).isNull()
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.back_home_safe)
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD)).isNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.pointsCoordinateLD)).isNull()
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
    fun fetchTrip_emitSchedulePointCoordinate(){
        viewModel.fetchAndDisplayUserActiveTrip()
        assertThat(LiveDataTestUtil.getValue(viewModel.pointsCoordinateLD)[1].keys)
            .containsAtLeastElementsIn(tripWithData.points
                .filter { it.pointTrip.typePoint == TypePoint.SCHEDULE_STAGE }
                .map { it.pointTrip.id })
    }

    @Test
    fun fetchTrip_emitStartAndEndPointCoordinate(){
        viewModel.fetchAndDisplayUserActiveTrip()
        assertThat(LiveDataTestUtil.getValue(viewModel.pointsCoordinateLD)[0].keys)
            .containsAtLeast(tripWithData.points.find { it.pointTrip.typePoint == TypePoint.START }?.pointTrip?.id,
                tripWithData.points.find { it.pointTrip.typePoint == TypePoint.END }?.pointTrip?.id)

    }

    @Test
    fun fetchTrip_emitCheckedUpPointCoordinate(){
        viewModel.fetchAndDisplayUserActiveTrip()
        assertThat(LiveDataTestUtil.getValue(viewModel.pointsCoordinateLD)[2].keys)
            .containsExactlyElementsIn(tripWithData.points
                .filter { it.pointTrip.typePoint == TypePoint.CHECKED_UP }
                .map { it.pointTrip.id })

    }

    @Test
    fun fetchAndDisplayUserActiveTrip_showTripInfo(){
        viewModel.fetchAndDisplayUserActiveTrip()
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).userId).isEqualTo(tripWithData.trip.userId)
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).active).isTrue()

    }

    @Test
    fun clickPointMap_selectPointAndShowDetails(){
        viewModel.fetchAndDisplayUserActiveTrip()
        LiveDataTestUtil.getValue(viewModel.pointsCoordinateLD) //wait fro view model to fetch the current trip
        viewModel.clickPointTrip(tripWithData.points[0].pointTrip.id)
        assertThat(tripRepository.pointSelected).isEqualTo(tripWithData.points[0])
        val value = LiveDataTestUtil.getValue(viewModel.showPointDetailsLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun stopTrip_cancelCheckUpWorker(){
        viewModel.fetchAndDisplayUserActiveTrip()
        LiveDataTestUtil.getValue(viewModel.pointsCoordinateLD) //wait fro view model to fetch the current trip
        viewModel.clickStartStop()
        val value = LiveDataTestUtil.getValue(viewModel.cancelCheckUpWorkerLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()

    }

    @Test
    fun noInternet_showMessage(){
        viewModel.showNeedInternetMessage()
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.need_internet)
    }
}