package com.galou.watchmyback.detailsTrip

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TripStatus
import com.galou.watchmyback.data.entity.TypePoint
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.repository.FakeTripRepositoryImpl
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.testHelpers.LiveDataTestUtil
import com.galou.watchmyback.testHelpers.mainUser
import com.galou.watchmyback.testHelpers.preferencesTest
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
 * 2019-12-18
 */
class DetailsTripViewModelTest {

    private lateinit var viewModel: DetailsTripViewModel
    private lateinit var tripRepository: FakeTripRepositoryImpl
    private lateinit var userRepository: UserRepository

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
        viewModel = DetailsTripViewModel(tripRepository, userRepository)
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
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).trip.id).isEqualTo(tripWithData.trip.id)
    }

    @Test
    fun fetchTripNoID_getActiveTripUser(){
        viewModel.fetchTripInfo()
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).trip.active).isTrue()
        assertThat(LiveDataTestUtil.getValue(viewModel.tripLD).trip.userId).isEqualTo(mainUser.id)
    }

    @Test
    fun fetchTrip_emitStartPointInfo(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.startPointLD)).isEqualTo(tripWithData.points.first { it.pointTrip.typePoint == TypePoint.START })
    }

    @Test
    fun fetchTrip_emitEndPointInfo(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.endPointLD)).isEqualTo(tripWithData.points.first { it.pointTrip.typePoint == TypePoint.END })
    }

    @Test
    fun fetchTrip_emitLastPointInfo(){
        viewModel.fetchTripInfo(tripWithData.trip.id)
        assertThat(LiveDataTestUtil.getValue(viewModel.lastPointCheckedLD)).isEqualTo(tripWithData.points.findLast { it.pointTrip.typePoint == TypePoint.CHECKED_UP })
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
}