package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.applicationUse.TripDisplay
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.data.remoteDBObject.TripWithDataRemoteDB
import com.galou.watchmyback.utils.todaysDate
import java.text.SimpleDateFormat

/**
 * @author galou
 * 2019-12-09
 */

/**
 * Convert a [TripWithData] to a [TripWithDataRemoteDB]
 * Trip in the remote Database stored only the id of the watchers instead of the all [User] object
 *
 * @return a [TripWithDataRemoteDB] object with the same data than the TripWithData
 */
fun TripWithData.convertForRemoteDB(): TripWithDataRemoteDB {
    with(this){
        return TripWithDataRemoteDB(
            trip = trip,
            points = points,
            watchersId = watchers.map { it.id }
        )
    }
}

/**
 * Convert a [TripWithDataRemoteDB] to a [TripWithData]
 * Trip in the remote Database stored only the id of the watchers instead of the all [User] object
 *
 * @return a [TripWithData] object with the same data than the TripWithDataRemoteDB
 */
fun TripWithDataRemoteDB.convertForLocal(watchers: List<User>): TripWithData {
    with(this){
        return TripWithData(
            trip = trip,
            points = points,
            watchers = watchers.toMutableList()
        )
    }
}

/**
 * Convert a [TripWithData] into a [TripDisplay] object to display the trip on the recycler view easily
 *
 * @param userPreferences User preferecne to convert time and temeprature display
 * @param ownerName Trip owner username
 * @return
 */
fun TripWithData.convertForDisplay(userPreferences: UserPreferences, ownerName: String): TripDisplay {
    val timeFormatter = SimpleDateFormat(userPreferences.timeDisplay.displayTimePattern)
    val startPoint = points.find { it.pointTrip.typePoint == TypePoint.START }!!.pointTrip
    val endPoint = points.find { it.pointTrip.typePoint == TypePoint.END }!!
    val latestPoint = points.findLatestCheckUpPoint()
        ?: endPoint
    return TripDisplay(
        tripId = trip.id,
        tripType = trip.type ?: throw Exception("Missing trip type $this"),
        tripLocation = trip.mainLocation ?: "",
        tripStatus = trip.status,
        startTime = timeFormatter.format(startPoint.time ?: throw Exception("Missing start time $this")),
        endTime = timeFormatter.format(endPoint.pointTrip.time ?: throw Exception("Missing end time $this")),
        tripOwnerName = ownerName,
        weatherCondition = latestPoint.weatherData?.condition ?: throw Exception("Missing weather condition $this"),
        temperature = when(userPreferences.unitSystem){
            UnitSystem.METRIC -> "${latestPoint.weatherData.temperature?.kelvinToCelsius()}°C"
            UnitSystem.IMPERIAL -> "${latestPoint.weatherData.temperature?.kelvinToFahrenheit()}°F"
        }
    )
}

fun TripWithData.updateStatus(){
    if(trip.status != TripStatus.BACK_SAFE){
        val checkedUpPoints = points.filter { it.pointTrip.typePoint == TypePoint.CHECKED_UP }
        val backTime = points.find { it.pointTrip.typePoint == TypePoint.END }!!.pointTrip.time
        trip.status = when {
            todaysDate.after(backTime) && todaysDate.time - backTime!!.time > 3600000 -> TripStatus.REALLY_LATE
            todaysDate.after(backTime) && checkedUpPoints.isEmpty() -> TripStatus.LATE_NO_NEWS
            checkedUpPoints.isEmpty() -> TripStatus.ON_GOING_NO_NEWS
            todaysDate.after(backTime) -> {
                val latestCheckup = checkedUpPoints.findLatestCheckUpPoint()!!.pointTrip.time!!
                if (todaysDate.time - latestCheckup.time > trip.updateFrequency!!.frequencyMillisecond){
                    TripStatus.LATE_NO_NEWS
                } else TripStatus.LATE_EMITTING
            }

            else -> TripStatus.ON_GOING
        }
    }
}

fun List<TripWithData>.updateStatus(){
    forEach { it.updateStatus() }
}