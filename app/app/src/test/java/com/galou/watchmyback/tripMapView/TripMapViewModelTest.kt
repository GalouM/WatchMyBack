package com.galou.watchmyback.tripMapView

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.galou.watchmyback.R
import com.galou.watchmyback.WatchMyBackApplication
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
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

/**
 * @author galou
 * 2019-11-30
 */
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
class TripMapViewModelTest {

    private lateinit var viewModel: TripMapViewModel
    private lateinit var tripRepository: FakeTripRepositoryImpl
    private lateinit var userRepository: FakeUserRepositoryImpl
    private lateinit var context: Context

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setupViewModel(){
        Dispatchers.setMain(mainThreadSurrogate)
        context = ApplicationProvider.getApplicationContext<WatchMyBackApplication>()
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
        viewModel.clickStartStop(context)
        val value = LiveDataTestUtil.getValue(viewModel.openAddTripActivity)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickStopTrip_showMessage(){
        viewModel.fetchAndDisplayUserActiveTrip()
        LiveDataTestUtil.getValue(viewModel.tripLD)
        viewModel.clickStartStop(context)
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
}