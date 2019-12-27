package com.galou.watchmyback.mapPickLocation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.Location
import com.galou.watchmyback.data.entity.PointTrip
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.repository.FakeTripRepositoryImpl
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.testHelpers.LiveDataTestUtil
import com.galou.watchmyback.testHelpers.assertSnackBarMessage
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
 * 2019-12-15
 */
class PickLocationMapViewModelTest {

    private lateinit var viewModel: PickLocationMapViewModel

    private lateinit var tripRepository: TripRepository

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private val point1 = PointTripWithData(
        pointTrip = PointTrip(),
        location = Location(latitude = 123.456, longitude = 543.321)
    )

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setupViewModel(){
        Dispatchers.setMain(mainThreadSurrogate)
        tripRepository = FakeTripRepositoryImpl()
        tripRepository.pointSelected = point1
        viewModel = PickLocationMapViewModel(tripRepository)

    }

    @ExperimentalCoroutinesApi
    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    fun init_showHelperMessage(){
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.click_on_map)
    }

    @Test
    fun onMapReadyNoPointsLocation_showNothing(){
        tripRepository.pointSelected = PointTripWithData()
        viewModel.onMapReady()
        assertThat(LiveDataTestUtil.getValue(viewModel.pointSelectedLocation)).isNull()
        assertThat(LiveDataTestUtil.getValue(viewModel.pointsTripLocationLD)).isEmpty()
    }

    @Test
    fun onMapReadyWithPointsInformation_showPoints(){
        val point2 = PointTripWithData(
            pointTrip = PointTrip(),
            location = Location(latitude = 987.654, longitude = 567.34)
        )
        val point3 = PointTripWithData(
            pointTrip = PointTrip(),
            location = Location(latitude = 65.33, longitude = 85.34)
        )
        tripRepository.tripPoints = listOf(point2, point3)

        viewModel.onMapReady()
        val pointSelectedValue = LiveDataTestUtil.getValue(viewModel.pointSelectedLocation).getContentIfNotHandled()
        println(pointSelectedValue)
        assertThat(pointSelectedValue).isNotNull()
        assertThat(pointSelectedValue?.latitude).isEqualTo(point1.location?.latitude)
        assertThat(pointSelectedValue?.longitude).isEqualTo(point1.location?.longitude)
        val tripPointsValue = LiveDataTestUtil.getValue(viewModel.pointsTripLocationLD)
        assertThat(tripPointsValue).isNotNull()
        assertThat(tripPointsValue[0].latitude).isEqualTo(point2.location?.latitude)
        assertThat(tripPointsValue[0].longitude).isEqualTo(point2.location?.longitude)
        assertThat(tripPointsValue[1].latitude).isEqualTo(point3.location?.latitude)
        assertThat(tripPointsValue[1].longitude).isEqualTo(point3.location?.longitude)
    }

    @Test
    fun requestCenterCameraOnUser_emitLiveData(){
        viewModel.centerCameraOnUser()
        assertThat(LiveDataTestUtil.getValue(viewModel.centerCameraLD).getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun updatePointPosition_updatePositionOnTheMap(){
        val newLatitude = 654.765
        val newLongitude = 984.864
        viewModel.updatePointPosition(newLatitude, newLongitude)
        val value = LiveDataTestUtil.getValue(viewModel.pointSelectedLocation).getContentIfNotHandled()
        assertThat(value).isNotNull()
        assertThat(value?.latitude).isEqualTo(newLatitude)
        assertThat(value?.longitude).isEqualTo(newLongitude)
    }

    @Test
    fun validateCoordinateWithNoNewCoordinate_showMessage(){
        viewModel.validateCoordinate()
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.click_on_map)

    }

    @Test
    fun validateCoordinateWithNewCoordinate_validateCoordinate(){
        val newLatitude = 654.765
        val newLongitude = 984.864
        viewModel.updatePointPosition(newLatitude, newLongitude)
        viewModel.validateCoordinate()
        val value = LiveDataTestUtil.getValue(viewModel.validateNewCoordinateLD).getContentIfNotHandled()
        assertThat(value).isNotNull()
        assertThat(value?.longitude).isEqualTo(newLongitude)
        assertThat(value?.latitude).isEqualTo(newLatitude)

    }
}