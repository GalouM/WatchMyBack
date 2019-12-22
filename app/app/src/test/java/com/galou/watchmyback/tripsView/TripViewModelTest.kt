package com.galou.watchmyback.tripsView

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.galou.watchmyback.data.applicationUse.TripDisplay
import com.galou.watchmyback.data.entity.TripStatus
import com.galou.watchmyback.data.entity.TripType
import com.galou.watchmyback.data.entity.WeatherCondition
import com.galou.watchmyback.testHelpers.LiveDataTestUtil
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

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = TripsViewModel()
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
}