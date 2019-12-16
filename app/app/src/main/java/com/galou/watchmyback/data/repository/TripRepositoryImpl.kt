package com.galou.watchmyback.data.repository

import com.galou.watchmyback.BuildConfig
import com.galou.watchmyback.data.api.GeocodingApiService
import com.galou.watchmyback.data.api.OpenWeatherService
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.source.local.TripLocalDataSource
import com.galou.watchmyback.data.source.remote.TripRemoteDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.extension.toWeatherConditionName
import com.galou.watchmyback.utils.returnSuccessOrError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * @author galou
 * 2019-12-08
 */
class TripRepositoryImpl(
    private val geocodingApiService: GeocodingApiService,
    private val weatherService: OpenWeatherService,
    private val localSource: TripLocalDataSource,
    private val remoteSource: TripRemoteDataSource
) : TripRepository {

    override var pointSelected: PointTripWithData? = null
    override var tripPoints: List<PointTripWithData>? = null

    /**
     * Run Async task to create a trip in the remote and local database
     *
     * @param trip [TripWithData] to create
     * @param checkList [CheckListWithItems] assigned to the trip
     * @return [Result] of the operation
     *
     * @see deletePreviousActiveTrip
     */
    override suspend fun createTrip(trip: TripWithData, checkList: CheckListWithItems?): Result<Void?> = coroutineScope {
        return@coroutineScope when (val deleteTask = deletePreviousActiveTrip(trip.trip.userId)){
            is Result.Success -> {
                val localTask = async { localSource.createTrip(trip, checkList) }
                val remoteTask = async { remoteSource.createTrip(trip, checkList) }
                returnSuccessOrError(localTask.await(), remoteTask.await())
            }
            else -> deleteTask
        }

    }

    /**
     * Update a list of points with location and weather information
     *
     * @param points list of points to update
     * @return [Result] of the operation
     *
     * @see getPointLocationInformation
     * @see getPointWeatherData
     */
    override suspend fun fetchPointLocationInformation(points: List<PointTripWithData>): Result<Void?> = coroutineScope {
        val locationTask = async { getPointLocationInformation(points) }
        val weatherTask = async { getPointWeatherData(points) }
        return@coroutineScope returnSuccessOrError(locationTask.await(), weatherTask.await())
    }

    /**
     * Update a list of points' location with a city and country name depending on their location
     *
     * @param pointTrips list of points top update
     * @return [Result] of the operation
     *
     * @see GeocodingApiService.getAddressFromLocation
     */
    private suspend fun getPointLocationInformation(pointTrips: List<PointTripWithData>): Result<Void?> = coroutineScope {
        var error: Int? = null
        pointTrips.forEach { point ->
            val location = "${point.location?.latitude},${point.location?.longitude}"
            launch {
                val apiResponse = geocodingApiService.getAddressFromLocation(
                    location = location,
                    apiKey = BuildConfig.GeocodingApiKey
                )
                if (apiResponse.isSuccessful){
                    with(apiResponse.body()!!.results[0].locations[0]){
                        point.location!!.city = city
                        point.location.country = countryCode
                    }
                } else {
                    error = apiResponse.code()
                }
            }
        }
        return@coroutineScope when(error){
            null -> Result.Success(null)
            else -> Result.Error(Exception("Error fetching location information $error"))
        }

    }

    /**
     * Update a list of points weather data
     *
     * @param pointTrips list of points to update
     *
     * @see OpenWeatherService.getWeatherLocation
     */
    private suspend fun getPointWeatherData(pointTrips: List<PointTripWithData>) = coroutineScope {
        var error: Int? = null
        pointTrips.forEach { point ->
            launch {
                val apiResponse = weatherService.getWeatherLocation(
                    latitude = point.location?.latitude ?: throw Exception("Missing point location $point"),
                    longitude = point.location.longitude ?: throw Exception("Missing point location $point"),
                    apiKey = BuildConfig.WeatherApiKey
                )

                if (apiResponse.isSuccessful){
                    with(apiResponse.body()!!){
                        point.weatherData!!.iconName = weather[0].icon
                        point.weatherData.temperature = main.temp
                        point.weatherData.dateTime = Date(dateTime.toLong())
                        point.weatherData.condition = weather[0].description.toWeatherConditionName()

                    }
                }
                else {
                    error = apiResponse.code()
                }
            }
        }

        return@coroutineScope when(error){
            null -> Result.Success(null)
            else -> Result.Error(Exception("Error fetching location information $error"))
        }
    }

    /**
     * Delete all the user's active trips from the remote and local database
     *
     * @param userId ID of the user
     * @return [Result] of the operation
     */
    private suspend fun deletePreviousActiveTrip(userId: String): Result<Void?> = coroutineScope {
        val localTask = async { localSource.deleteActiveTrip(userId) }
        val remoteTask = async { remoteSource.deleteActiveTrip(userId) }

        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())

    }


    /**
     * Fetch the user active trip
     *
     * @param userId ID of the user
     * @return [Result] of the operation with a [TripWithData]
     */
    override suspend fun fetchUserActiveTrip(userId: String): Result<TripWithData?> {
        return when(val remoteTask = remoteSource.fetchActiveTrip(userId)){
            is Result.Success -> remoteTask
            else -> localSource.fetchActiveTrip(userId)
            }
    }
}