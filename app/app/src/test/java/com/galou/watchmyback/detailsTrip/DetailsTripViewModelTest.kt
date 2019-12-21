package com.galou.watchmyback.detailsTrip

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TripStatus
import com.galou.watchmyback.data.entity.TypePoint
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.repository.FakeCheckListRepository
import com.galou.watchmyback.data.repository.FakeTripRepositoryImpl
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.data.repository.UserRepository
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
import org.koin.core.context.stopKoin

/**
 * @author galou
 * 2019-12-18
 */
class DetailsTripViewModelTest {

    private lateinit var viewModel: DetailsTripViewModel
    private lateinit var tripRepository: FakeTripRepositoryImpl
    private lateinit var userRepository: UserRepository
    private lateinit var checkListRepository: FakeCheckListRepository

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        Dispatchers.setMain(mainThreadSurrogate)
        tripRepository = FakeTripRepositoryImpl()
        userRepository = FakeUserRepositoryImpl()
        userRepository.currentUser.value = mainUser
        userRepository.userPreferences.value = preferencesTest
        checkListRepository = FakeCheckListRepository()
        viewModel = DetailsTripViewModel(tripRepository, userRepository, checkListRepository)
    }

    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    fun fetchTripWithID_getTripWithSameID(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).id).isEqualTo(tripWithData.trip.id)
    }

    @Test
    fun fetchTripNoID_getActiveTripUser(){
        viewModel.fetchTripInfo()
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).active).isTrue()
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).userId).isEqualTo(mainUser.id)
    }

    @Test
    fun fetchTrip_emitStartPointTime(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.startPointTimeLD))
            .isEqualTo(tripWithData.points.first { it.pointTrip.typePoint == TypePoint.START }.pointTrip.time)
    }

    @Test
    fun fetchTrip_emitEndPointTime(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.endPointTimeLD))
            .isEqualTo(tripWithData.points.first { it.pointTrip.typePoint == TypePoint.END }.pointTrip.time)
    }

    @Test
    fun fetchTrip_emitStartPointWeather(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.startPointWeatherLD))
            .isEqualTo(tripWithData.points.first { it.pointTrip.typePoint == TypePoint.START }.weatherData)
    }

    @Test
    fun fetchTrip_emitEndPointWeather(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.endPointWeatherLD))
            .isEqualTo(tripWithData.points.first { it.pointTrip.typePoint == TypePoint.END }.weatherData)
    }

    @Test
    fun fetchTrip_emitLastPointInfo(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.lastPointCheckedLD))
            .isEqualTo(tripWithData.points.findLast { it.pointTrip.typePoint == TypePoint.CHECKED_UP })
    }

    @Test
    fun fetchTrip_emitSchedulePointCoordinate(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.schedulePointsLD).keys)
            .containsAtLeastElementsIn(tripWithData.points
                .filter { it.pointTrip.typePoint == TypePoint.SCHEDULE_STAGE }
                .map { it.pointTrip.id })
        assertThat(LiveDataTestUtil.getValue(viewModel.schedulePointsLD).keys)
            .containsAtLeast(tripWithData.points.find { it.pointTrip.typePoint == TypePoint.START }?.pointTrip?.id,
            tripWithData.points.find { it.pointTrip.typePoint == TypePoint.END }?.pointTrip?.id)
    }

    @Test
    fun fetchTrip_emitCheckedUpPointCoordinate(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.checkedPointsLD).keys)
            .containsExactlyElementsIn(tripWithData.points
                .filter { it.pointTrip.typePoint == TypePoint.CHECKED_UP }
                .map { it.pointTrip.id })

    }

    @Test
    fun fetchTrip_emitIsTripDoneCorrectly(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.tripIsDoneLD)).isEqualTo(tripWithData.trip.status == TripStatus.BACK_SAFE)
    }

    @Test
    fun userInfoEmittedOnInit(){
        assertThat(LiveDataTestUtil.getValue(viewModel.userLD)).isEqualTo(mainUser)
    }

    @Test
    fun userPrefsEmittedOnInit(){
        assertThat(LiveDataTestUtil.getValue(viewModel.userPrefsLD)).isEqualTo(preferencesTest)
    }
    
    @Test
    fun clickCallEmergencyEmitEventWithNumber(){
        viewModel.callEmergency()
        val value = LiveDataTestUtil.getValue(viewModel.emergencyNumberLD).getContentIfNotHandled()
        assertThat(value).isEqualTo(userRepository.userPreferences.value?.emergencyNumber)
    }
    
    @Test
    fun clickCallEmergency_noEmergencyNumberSetup_showMessage(){
        userRepository.userPreferences.value = UserPreferences(emergencyNumber = "")
        LiveDataTestUtil.getValue(viewModel.userPrefsLD)
        println(LiveDataTestUtil.getValue(viewModel.snackbarMessage))
        println(R.string.no_emergency_number)
        //assertSnackBarMessage(viewModel.snackbarMessage, R.string.no_emergency_number)
    }

    @Test
    fun fetchTripWithId_emitTripOwnerName(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.tripOwnerNameLD)).isEqualTo(mainUser.username)
    }

    @Test
    fun fetchTripActive_tripOwnerIsNull(){
        viewModel.fetchTripInfo()
        assertThat(LiveDataTestUtil.getValue(viewModel.tripOwnerNameLD)).isNull()
    }

    @Test
    fun callTripOwnerWithPhoneNumber(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        LiveDataTestUtil.getValue(viewModel.tripOwnerNameLD)
        viewModel.callTripOwner()
        val value = LiveDataTestUtil.getValue(viewModel.tripOwnerNumberLD).getContentIfNotHandled()
        assertThat(value).isEqualTo(mainUser.phoneNumber)
    }

    @Test
    fun fetchTrip_emitItemCheckList(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.itemsCheckListLD)).containsExactlyElementsIn(itemList1)
    }

    @Test
    fun clickCenterCameraOnUser_centerCameraOnUser(){
        viewModel.centerCameraOnUser()
        val value = LiveDataTestUtil.getValue(viewModel.centerCameraUserLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun gpsNotAvailable_showMessage(){
        viewModel.gpsNotAvailable()
        assertSnackBarMessage(viewModel.snackbarMessage, R.string.turn_on_gps)
    }

    @Test
    fun clickPointMap_selectPointAndShowDetails(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        LiveDataTestUtil.getValue(viewModel.startPointTimeLD) //wait fro view model to fetch the current trip
        viewModel.clickPointTrip(tripWithData.points[0].pointTrip.id)
        assertThat(tripRepository.pointSelected).isEqualTo(tripWithData.points[0])
        val value = LiveDataTestUtil.getValue(viewModel.showPointDetailsLD)
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

}