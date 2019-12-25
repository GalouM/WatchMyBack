package com.galou.watchmyback.tripsView

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.data.applicationUse.TripDisplay
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.data.repository.FakeTripRepositoryImpl
import com.galou.watchmyback.data.repository.FakeUserRepositoryImpl
import com.galou.watchmyback.testHelpers.LiveDataTestUtil
import com.galou.watchmyback.testHelpers.mainUser
import com.galou.watchmyback.testHelpers.tripWithData
import com.galou.watchmyback.utils.extension.convertForDisplay
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
 * 2019-12-21
 */
class TripViewModelTest {

    private lateinit var viewModel: TripsViewModel
    private lateinit var tripRepository: FakeTripRepositoryImpl
    private lateinit var userRepository: FakeUserRepositoryImpl
    private val userPreferences = UserPreferences(timeDisplay = TimeDisplay.H_24, unitSystem = UnitSystem.METRIC)

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        Dispatchers.setMain(mainThreadSurrogate)
        tripRepository = FakeTripRepositoryImpl()
        userRepository = FakeUserRepositoryImpl()
        userRepository.currentUser.value = mainUser
        userRepository.userPreferences.value = userPreferences
        viewModel = TripsViewModel(userRepository, tripRepository)
    }

    @After
    fun close(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        stopKoin()
    }

    @Test
    fun clickTrip_openDetailsTrips(){
        val trip = TripDisplay(
            tripId = "tripId",
            tripLocation = "",
            tripStatus = TripStatus.BACK_SAFE,
            startTime = "",
            endTime = "",
            weatherCondition = WeatherCondition.RAIN,
            temperature = "",
            tripOwnerName = "",
            tripType = TripType.BIKING
        )
        viewModel.onClickTrip(trip)
        val value = LiveDataTestUtil.getValue(viewModel.tripSelectedLD)
        assertThat(value.getContentIfNotHandled()).contains(trip.tripId)
    }

    @Test
    fun onFetchTripsWatching_emitListTrips(){
        viewModel.fetchTripsWatching()
        assertThat(LiveDataTestUtil.getValue(viewModel.tripsLD)).contains(tripWithData.convertForDisplay(userPreferences, mainUser.username!!))
    }

    @Test
    fun onRefresh_emitListTrips(){
        viewModel.refreshTripList()
        assertThat(LiveDataTestUtil.getValue(viewModel.tripsLD)).contains(tripWithData.convertForDisplay(userPreferences, mainUser.username!!))
    }


}