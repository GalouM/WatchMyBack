package com.galou.watchmyback.data.api.reponse


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * API response from "https://openweathermap.org/"
 *
 * @property weather list of weather data
 * @property dateTime time of the weather condition
 * @property main temperature information
 */
@JsonClass(generateAdapter = true)
data class OpenWeatherMapApiResponse(
    @Json(name = "dt")
    val dateTime: Int,
    @Json(name = "main")
    val main: Main,
    @Json(name = "weather")
    val weather: List<Weather>
)

/**
 * API response from "https://openweathermap.org/"
 *
 * @property temp temperature in Kelvin
 */
@JsonClass(generateAdapter = true)
data class Main(
    @Json(name = "temp")
    val temp: Double
)

/**
 * API response from "https://openweathermap.org/"
 *
 * @property description description of the weather condition
 * @property icon
 */
@JsonClass(generateAdapter = true)
data class Weather(
    @Json(name = "description")
    val description: String,
    @Json(name = "icon")
    val icon: String
)