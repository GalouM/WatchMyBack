package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.TimeDisplay
import com.galou.watchmyback.data.entity.TypePoint
import com.galou.watchmyback.data.entity.UnitSystem
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * @author galou
 * 2019-11-07
 */

class IntExtUnitTest {

    @Test
    @Throws(Exception::class)
    fun radioButtonUnitSystem_clickSendRightValue(){
        val buttonMetricId = R.id.settings_view_unit_system_unit_metric
        assertThat(buttonMetricId.onClickUnitSystem()).isEqualTo(UnitSystem.METRIC)

        val buttonImperialId = R.id.settings_view_unit_system_unit_imperial
        assertThat(buttonImperialId.onClickUnitSystem()).isEqualTo(UnitSystem.IMPERIAL)

    }

    @Test
    @Throws(Exception::class)
    fun radioButtonTime_clickSendRightValue(){
        val button12Id = R.id.settings_view_unit_system_time_12
        assertThat(button12Id.onClickTimeDisplay()).isEqualTo(TimeDisplay.H_12)

        val button24Id = R.id.settings_view_unit_system_time_24
        assertThat(button24Id.onClickTimeDisplay()).isEqualTo(TimeDisplay.H_24)
    }

    @Test
    @Throws(Exception::class)
    fun buttonFromStartPointUserLocation_returnAStartButtonWithCorrectTripID(){
        val idButton = R.id.add_trip_start_point_user_location
        val tripId = "TripId"
        val point = idButton.idButtonToPointTrip(tripId)
        assertThat(point.pointTrip.tripId).isEqualTo(tripId)
        assertThat(point.pointTrip.typePoint).isEqualTo(TypePoint.START)
    }

    @Test
    @Throws(Exception::class)
    fun buttonFromStartPointMap_returnAStartButtonWithCorrectTripID(){
        val idButton = R.id.add_trip_start_point_pick
        val tripId = "TripId"
        val point = idButton.idButtonToPointTrip(tripId)
        assertThat(point.pointTrip.tripId).isEqualTo(tripId)
        assertThat(point.pointTrip.typePoint).isEqualTo(TypePoint.START)
    }

    @Test
    @Throws(Exception::class)
    fun buttonFromEndPointUserLocation_returnAStartButtonWithCorrectTripID(){
        val idButton = R.id.add_trip_end_point_user_location
        val tripId = "TripId"
        val point = idButton.idButtonToPointTrip(tripId)
        assertThat(point.pointTrip.tripId).isEqualTo(tripId)
        assertThat(point.pointTrip.typePoint).isEqualTo(TypePoint.END)
    }

    @Test
    @Throws(Exception::class)
    fun buttonFromEndPointMap_returnAStartButtonWithCorrectTripID(){
        val idButton = R.id.add_trip_end_point_pick
        val tripId = "TripId"
        val point = idButton.idButtonToPointTrip(tripId)
        assertThat(point.pointTrip.tripId).isEqualTo(tripId)
        assertThat(point.pointTrip.typePoint).isEqualTo(TypePoint.END)
    }
}