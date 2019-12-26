package com.galou.watchmyback.data.repository

import com.galou.watchmyback.BuildConfig
import com.galou.watchmyback.data.api.GeocodingApiService
import com.galou.watchmyback.data.api.OpenWeatherService
import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.data.applicationUse.TripDisplay
import com.galou.watchmyback.data.entity.CheckListWithItems
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.source.local.TripLocalDataSource
import com.galou.watchmyback.data.source.remote.TripRemoteDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.extension.addCheckUpPoint
import com.galou.watchmyback.utils.extension.convertForDisplay
import com.galou.watchmyback.utils.extension.isRecent
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
    override suspend fun fetchPointLocationInformation(vararg points: PointTripWithData): Result<Void?> = coroutineScope {
        val locationTask = async { getPointLocationInformation(*points) }
        val weatherTask = async { getPointWeatherData(*points) }
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
    private suspend fun getPointLocationInformation(vararg pointTrips: PointTripWithData): Result<Void?> = coroutineScope {
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
    private suspend fun getPointWeatherData(vararg pointTrips: PointTripWithData) = coroutineScope {
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
     *
     * @see TripLocalDataSource.deleteActiveTrip
     * @see TripRemoteDataSource.deleteActiveTrip
     */
    private suspend fun deletePreviousActiveTrip(userId: String): Result<Void?> = coroutineScope {
        val localTask = async { localSource.deleteActiveTrip(userId) }
        val remoteTask = async { remoteSource.deleteActiveTrip(userId) }

        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())

    }


    /**
     * Fetch the user active trip from the remote database
     * If an error happened the data will be fetched from the local database
     *
     * @param userId ID of the user
     * @return [Result] of the operation with a [TripWithData]
     *
     * @see TripRemoteDataSource.fetchActiveTrip
     * @see TripLocalDataSource.fetchActiveTrip
     */
    override suspend fun fetchUserActiveTrip(userId: String, refresh: Boolean): Result<TripWithData?> {
        if (refresh) {
            when (val remoteTask = remoteSource.fetchActiveTrip(userId)) {
                is Result.Success -> {
                    remoteTask.data?.let {
                         return when(val localCopyTask = localSource.createTrip(it, null)){
                            is Result.Canceled -> Result.Canceled(localCopyTask.exception)
                            is Result.Error -> Result.Error(localCopyTask.exception)
                            is Result.Success -> remoteTask
                        }
                    }
                    return remoteTask
                }

            }
        }
        return localSource.fetchActiveTrip(userId)

    }

    /**
     * Fetch a trip with the corresponding ID from the remote database
     * If an error happended the data data will be fetched from the local database
     *
     * @param tripId Id of the trip to fetch
     * @return [Result] of the operation with a [TripWithData]
     *
     * @see TripRemoteDataSource.fetchTrip
     * @see TripLocalDataSource.fetchTrip
     */
    override suspend fun fetchTrip(tripId: String): Result<TripWithData?> {
        return when(val remoteTask = remoteSource.fetchTrip(tripId)){
            is Result.Success -> remoteTask
            else -> localSource.fetchTrip(tripId)
        }

    }

    /**
     * Fetch all the trips a user is watching
     *
     * @param userId Id of the user
     * @return [Result] of the operation with a list of [TripWithData]
     *
     * @see TripLocalDataSource.fetchTripUserWatching
     * @see TripRemoteDataSource.fetchTripUserWatching
     */
    override suspend fun fetchTripUserWatching(userId: String): Result<List<TripWithData>> {
        return when(val remoteTask = remoteSource.fetchTripUserWatching(userId)){
            is Result.Success -> Result.Success(keepOnlyRecentTrip(remoteTask.data))
            else -> localSource.fetchTripUserWatching(userId)
        }
    }

    /**
     * Delete all the trips from the database that have been finished for over a week
     *
     * @param trips trips from the database
     * @return list of all the trips kept
     */
    private suspend fun keepOnlyRecentTrip(trips: List<TripWithData>): List<TripWithData>{
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val lastWeek = calendar.time
        val recentTrip = mutableListOf<TripWithData>()
        coroutineScope {
            trips.forEach { trip ->
                if (!trip.isRecent(lastWeek)){
                    launch { localSource.deleteTrip(trip) }
                    launch { remoteSource.deleteTrip(trip ) }

                } else {
                    recentTrip.add(trip)
                }
            }
        }
        return recentTrip

    }

    /**
     * Convert a list [TripWithData] into [TripDisplay] to be displayed by the view with all the necessary info
     *
     * @param trips [TripWithData] to convert
     * @param userPrefs [UserPreferences] to convert temperature and time according to the user's preferences
     * @return [Result] of the operation with a list of [TripDisplay]
     */
    override suspend fun convertTripForDisplay(trips: List<TripWithData>, userPrefs: UserPreferences): Result<List<TripDisplay>> = coroutineScope {
        val tripsForDisplay = mutableListOf<TripDisplay>()
        var error = false
        trips.forEach { trip ->
            launch {
                when(val remoteResult = remoteSource.fetchTripOwner(trip.trip.userId)){
                    is Result.Success ->
                        tripsForDisplay.add(
                            trip.convertForDisplay(
                                userPreferences = userPrefs,
                                ownerName = remoteResult.data.username ?: throw Exception("No username for user ${remoteResult.data}")))

                    else -> error = true
                }
            }
        }
        return@coroutineScope if (!error) Result.Success(tripsForDisplay)
        else Result.Error(Exception("Error while fetching trip's owner"))

    }

    override suspend fun updateTripStatus(trip: TripWithData): Result<Void?> = coroutineScope {
        val localTask = async { localSource.updateTripStatus(trip) }
        val remoteTask = async { remoteSource.updateTripStatus(trip) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())

    }

    override suspend fun createCheckUpPoint(currentUserId: String, coordinate: Coordinate): Result<Void?> {
        return when (val tripFetch = fetchUserActiveTrip(currentUserId, false)){
            is Result.Success -> {
                if (tripFetch.data != null){
                    val point = tripFetch.data.addCheckUpPoint(coordinate)
                    when(val fetchLocationTask = fetchPointLocationInformation(point)){
                        is Result.Success -> updatePointsTrip(tripFetch.data)
                        else -> fetchLocationTask
                    }

                } else {
                    Result.Error(Exception("No Trip found"))
                }

            }
            is Result.Error -> Result.Error(tripFetch.exception)
            is Result.Canceled -> Result.Canceled(tripFetch.exception)
        }
    }

    override suspend fun updatePointsTrip(trip: TripWithData): Result<Void?> = coroutineScope {
        val localTask = async { localSource.updateTripPoints(trip) }
        val remoteTask = async { remoteSource.updateTripPoints(trip) }
        return@coroutineScope returnSuccessOrError(localTask.await(), remoteTask.await())
    }
}