package com.galou.watchmyback.tripMapView

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
 * 2019-11-30
 */
class TripMapViewModelTest {

    private lateinit var viewModel: TripMapViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = TripMapViewModel()
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
}