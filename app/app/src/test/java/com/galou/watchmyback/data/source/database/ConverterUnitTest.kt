package com.galou.watchmyback.data.source.database

import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.utils.todaysDate
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Created by galou on 2019-10-21
 */

class ConverterUnitTest{

    private val tripType = TripType.BIKING
    private val tripTypeId = tripType.typeNameId

    private val tripStatus = TripStatus.REALLY_LATE
    private val tripStatusId = tripStatus.statusNameId

    private val updateFrequency = TripUpdateFrequency.ONE_HOUR
    private val frequencyMilli = updateFrequency.frequencyMillisecond

    private val pointType = TypePoint.END
    private val pointTypeName = pointType.typeName

    private val weatherType = WeatherCondition.MIST
    private val weatherConditionId = weatherType.conditionNameId

    private val date = todaysDate
    private val dateInMilli = date.time

    @Test
    fun checkConverterTripType_fromId(){
        assertThat(Converters.fromTripType(tripType)).isEqualTo(tripTypeId)
    }

    @Test
    fun checkConverterId_toTripType(){
        assertThat(Converters.toTripType(tripTypeId)).isEqualTo(tripType)
    }

    @Test
    fun checkConvertTripStatus_fromID(){
        assertThat(Converters.fromTripStatus(tripStatus)).isEqualTo(tripStatusId)
    }

    @Test
    fun checkConvertId_toTripStatus(){
        assertThat(Converters.toTripStatus(tripStatusId)).isEqualTo(tripStatus)
    }

    @Test
    fun checkConvertUpdateHz_fromMilli(){
        assertThat(Converters.fromTripUpdateFrequency(updateFrequency)).isEqualTo(frequencyMilli)
    }

    @Test
    fun checkConvertMilli_toUpdateHz(){
        assertThat(Converters.toTripUpdateFrequency(frequencyMilli)).isEqualTo(updateFrequency)
    }

    @Test
    fun checkConvertPointType_fromId(){
        assertThat(Converters.fromTypePoint(pointType)).isEqualTo(pointTypeName)
    }

    @Test
    fun checkConvertName_toPointType(){
        assertThat(Converters.toTypePoint(pointTypeName)).isEqualTo(pointType)
    }

    @Test
    fun checkConvertWeatherCondition_fromId(){
        assertThat(Converters.fromWeatherCondition(weatherType)).isEqualTo(weatherConditionId)
    }

    @Test
    fun checkConverterId_toWeatherCondition(){
        assertThat(Converters.toWeatherCondition(weatherConditionId)).isEqualTo(weatherType)
    }

    @Test
    fun checkConvertDate_fromMilli(){
        assertThat(Converters.dateToTimeStamp(date)).isEqualTo(dateInMilli)
    }

    @Test
    fun checkConvertMilli_toDate(){
        assertThat(Converters.fromTimeStamp(dateInMilli)).isEqualTo(date)
    }
}