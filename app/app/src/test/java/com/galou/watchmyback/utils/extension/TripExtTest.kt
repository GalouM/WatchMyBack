package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.data.remoteDBObject.TripWithDataRemoteDB
import com.galou.watchmyback.testHelpers.tripWithData
import com.galou.watchmyback.utils.todaysDate
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author galou
 * 2019-12-13
 */
class TripExtTest {

    @Test
    fun convertTripToTripDB_convertedCorrectly(){
        val trip = tripWithData
        val tripDB = trip.convertForRemoteDB()
        assertThat(tripDB.trip).isEqualTo(trip.trip)
        assertThat(tripDB.points).isEqualTo(trip.points)
        assertThat(tripDB.watchersId).containsExactlyElementsIn((trip.watchers.map { it.id }))
    }

    @Test
    fun convertTripDBToTrip_convertCorrectly(){
        val tripDB =
            TripWithDataRemoteDB()
        val listUser = mutableListOf<User>()
        tripDB.watchersId.forEach {
            listUser.add(User(id = it))
        }
        val trip = tripDB.convertForLocal(listUser)
        assertThat(trip.trip).isEqualTo(tripDB.trip)
        assertThat(trip.points).isEqualTo(tripDB.points)
        assertThat(trip.watchers.map { it.id }).containsExactlyElementsIn(tripDB.watchersId)
    }

    @Test
    fun convertTripForDisplay_convertCorrectly(){
        val startPoint = PointTripWithData(
            pointTrip = PointTrip(typePoint = TypePoint.START, time = todaysDate)
        )
        val endPoint = PointTripWithData(
            pointTrip = PointTrip(typePoint = TypePoint.END, time = todaysDate)
        )
        val checkUp = PointTripWithData(
            pointTrip = PointTrip(typePoint = TypePoint.CHECKED_UP, time = todaysDate),
            weatherData = WeatherData(condition = WeatherCondition.CLOUDS, temperature = 271.0)
        )
        val trip = TripWithData(
            trip = Trip(type = TripType.BIKING),
            watchers = mutableListOf(),
            points = mutableListOf(startPoint, endPoint, checkUp)
        )
        val userPreferences = UserPreferences(timeDisplay = TimeDisplay.H_12, unitSystem = UnitSystem.METRIC)
        val formatter = SimpleDateFormat(userPreferences.timeDisplay.displayTimePattern)
        val owner = User(username = "Test")
        val tripForDisplay = trip.convertForDisplay(userPreferences, owner.username!!)
        assertThat(tripForDisplay.tripId).isEqualTo(trip.trip.id)
        assertThat(tripForDisplay.tripType).isEqualTo(trip.trip.type)
        assertThat(tripForDisplay.tripStatus).isEqualTo(trip.trip.status)
        assertThat(tripForDisplay.tripLocation).isEqualTo(trip.trip.mainLocation)
        assertThat(tripForDisplay.tripOwnerName).isEqualTo(owner.username)
        assertThat(tripForDisplay.startTime).isEqualTo(formatter.format(startPoint.pointTrip.time!!))
        assertThat(tripForDisplay.endTime).isEqualTo(formatter.format(endPoint.pointTrip.time!!))
        assertThat(tripForDisplay.temperature).isEqualTo("${checkUp.weatherData!!.temperature?.kelvinToCelsius()}°C")
        assertThat(tripForDisplay.weatherCondition).isEqualTo(checkUp.weatherData!!.condition)
    }

    @Test
    fun updateTripStatusBackSafe_tripStatusSafe(){
        val trip = TripWithData(
            trip = Trip(status = TripStatus.BACK_SAFE),
            watchers = mutableListOf(),
            points = mutableListOf())
        trip.updateStatus()
        assertThat(trip.trip.status).isEqualTo(TripStatus.BACK_SAFE)
    }

    @Test
    fun updateTripStatusMoreThanAnHourLate_tripStatusReallyLate(){
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, -2)
        val endPoint = PointTripWithData(
            pointTrip = PointTrip(typePoint = TypePoint.END, time = calendar.time)
        )
        val trip = TripWithData(
            trip = Trip(status = TripStatus.ON_GOING),
            watchers = mutableListOf(),
            points = mutableListOf(endPoint))
        trip.updateStatus()
        assertThat(trip.trip.status).isEqualTo(TripStatus.REALLY_LATE)
    }

    @Test
    fun updateTripStatusLateNoCheckUpPoint_tripStatusLateNoNews(){
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, -30)
        val endPoint = PointTripWithData(
            pointTrip = PointTrip(typePoint = TypePoint.END, time = calendar.time)
        )
        val trip = TripWithData(
            trip = Trip(status = TripStatus.ON_GOING),
            watchers = mutableListOf(),
            points = mutableListOf(endPoint))
        trip.updateStatus()
        assertThat(trip.trip.status).isEqualTo(TripStatus.LATE_NO_NEWS)
    }

    @Test
    fun updateTripStatusNoCheckUpPoint_tripStatusOnGoingNoNews(){
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, 2)
        val endPoint = PointTripWithData(
            pointTrip = PointTrip(typePoint = TypePoint.END, time = calendar.time)
        )
        val trip = TripWithData(
            trip = Trip(status = TripStatus.ON_GOING),
            watchers = mutableListOf(),
            points = mutableListOf(endPoint))
        trip.updateStatus()
        assertThat(trip.trip.status).isEqualTo(TripStatus.ON_GOING_NO_NEWS)
    }

    @Test
    fun updateTripStatusWithNewCheckUpPointLate_tripStatusLateEmitting(){
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, -30)
        val endPoint = PointTripWithData(
            pointTrip = PointTrip(typePoint = TypePoint.END, time = calendar.time)
        )
        val calendarCheckUp = Calendar.getInstance()
        calendarCheckUp.add(Calendar.MINUTE, -5)
        val checkPoint = PointTripWithData(
            pointTrip = PointTrip(typePoint = TypePoint.CHECKED_UP, time = calendarCheckUp.time)
        )
        val trip = TripWithData(
            trip = Trip(status = TripStatus.ON_GOING, updateFrequency = TripUpdateFrequency.FIFTEEN_MINUTES),
            watchers = mutableListOf(),
            points = mutableListOf(endPoint, checkPoint))
        trip.updateStatus()
        assertThat(trip.trip.status).isEqualTo(TripStatus.LATE_EMITTING)
    }

    @Test
    fun updateTripStatusWithNoNewCheckUpPointLate_tripStatusLateNoNews(){
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, -30)
        val endPoint = PointTripWithData(
            pointTrip = PointTrip(typePoint = TypePoint.END, time = calendar.time)
        )
        val calendarCheckUp = Calendar.getInstance()
        calendarCheckUp.add(Calendar.MINUTE, -20)
        val checkPoint = PointTripWithData(
            pointTrip = PointTrip(typePoint = TypePoint.CHECKED_UP, time = calendarCheckUp.time)
        )
        val trip = TripWithData(
            trip = Trip(status = TripStatus.ON_GOING, updateFrequency = TripUpdateFrequency.FIFTEEN_MINUTES),
            watchers = mutableListOf(),
            points = mutableListOf(endPoint, checkPoint))
        trip.updateStatus()
        assertThat(trip.trip.status).isEqualTo(TripStatus.LATE_NO_NEWS)
    }

    @Test
    fun updateTripStatusWithCheckUpPoint_tripStatusOnGoing(){
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 30)
        val endPoint = PointTripWithData(
            pointTrip = PointTrip(typePoint = TypePoint.END, time = calendar.time)
        )
        val calendarCheckUp = Calendar.getInstance()
        calendarCheckUp.add(Calendar.MINUTE, -5)
        val checkPoint = PointTripWithData(
            pointTrip = PointTrip(typePoint = TypePoint.CHECKED_UP, time = calendarCheckUp.time)
        )
        val trip = TripWithData(
            trip = Trip(status = TripStatus.ON_GOING, updateFrequency = TripUpdateFrequency.FIFTEEN_MINUTES),
            watchers = mutableListOf(),
            points = mutableListOf(endPoint, checkPoint))
        trip.updateStatus()
        assertThat(trip.trip.status).isEqualTo(TripStatus.ON_GOING)
    }

    @Test
    fun tripAfterCertainDate_returnFalse(){
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -5)
        val fiveDayAgo = calendar.time


        val calendarEndPoint = Calendar.getInstance()
        calendarEndPoint.add(Calendar.DAY_OF_YEAR, -7)
        val endPoint = PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.END, time = calendarEndPoint.time))
        val trip = TripWithData(trip = Trip(), points = mutableListOf(endPoint), watchers = mutableListOf())

        assertThat(trip.isRecent(fiveDayAgo)).isFalse()

    }

    @Test
    fun tripBeforeCertainDate_returnTrue(){
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -5)
        val fiveDayAgo = calendar.time


        val calendarEndPoint = Calendar.getInstance()
        calendarEndPoint.add(Calendar.DAY_OF_YEAR, -3)
        val endPoint = PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.END, time = calendarEndPoint.time))
        val trip = TripWithData(trip = Trip(), points = mutableListOf(endPoint), watchers = mutableListOf())

        assertThat(trip.isRecent(fiveDayAgo)).isTrue()

    }

    @Test
    fun addCheckUpPoint_addPointCorrectly(){
        val newCoordinate = Coordinate(234.543,654.434)
        val newPoint = tripWithData.addCheckUpPoint(newCoordinate)
        assertThat(tripWithData.points).contains(newPoint)
        assertThat(newPoint.location?.latitude).isEqualTo(newCoordinate.latitude)
        assertThat(newPoint.location?.longitude).isEqualTo(newCoordinate.longitude)
        assertThat(newPoint.pointTrip.typePoint).isEqualTo(TypePoint.CHECKED_UP)
        assertThat(newPoint.pointTrip.tripId).isEqualTo(tripWithData.trip.id)
    }
}