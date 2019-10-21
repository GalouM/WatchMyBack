package com.galou.watchmyback.database

import com.galou.watchmyback.data.database.Converters
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.utils.todaysDate
import org.junit.Assert.assertEquals
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
        assertEquals(tripTypeId, Converters.fromTripType(tripType))
    }

    @Test
    fun checkConverterId_toTripType(){
        assertEquals(tripType, Converters.toTripType(tripTypeId))
    }

    @Test
    fun checkConvertTripStatus_fromID(){
        assertEquals(tripStatusId, Converters.fromTripStatus(tripStatus))
    }

    @Test
    fun checkConvertId_toTripStatus(){
        assertEquals(tripStatus, Converters.toTripStatus(tripStatusId))
    }

    @Test
    fun checkConvertUpdateHz_fromMilli(){
        assertEquals(frequencyMilli, Converters.fromTripUpdateFrequency(updateFrequency))
    }

    @Test
    fun checkConvertMilli_toUpdateHz(){
        assertEquals(updateFrequency, Converters.toTripUpdateFrequency(frequencyMilli))
    }

    @Test
    fun checkConvertPointType_fromId(){
        assertEquals(pointTypeName, Converters.fromTypePoint(pointType))
    }

    @Test
    fun checkConvertName_toPointType(){
        assertEquals(pointType, Converters.toTypePoint(pointTypeName))
    }

    @Test
    fun checkConvertWeatherCondition_fromId(){
        assertEquals(weatherConditionId, Converters.fromWeatherCondition(weatherType))
    }

    @Test
    fun checkConverterId_toWeatherCondition(){
        assertEquals(weatherType, Converters.toWeatherCondition(weatherConditionId))
    }

    @Test
    fun checkConvertDate_fromMilli(){
        assertEquals(dateInMilli, Converters.dateToTimeStamp(date))
    }

    @Test
    fun checkConvertMilli_toDate(){
        assertEquals(date, Converters.fromTimeStamp(dateInMilli))
    }
}