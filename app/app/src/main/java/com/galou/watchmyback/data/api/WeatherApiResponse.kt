package com.galou.watchmyback.data.api


import com.google.gson.annotations.SerializedName

/**
 * API response from "https://openweathermap.org/"
 *
 * @property city
 * @property list list of weather data
 */
data class WeatherApiResponse(
    @SerializedName("city")
    val city: City,
    @SerializedName("list")
    val list: List<DataWeatherFromAPI>
)

data class DataWeatherFromAPI(
    @SerializedName("dt")
    val dt: Int,
    @SerializedName("dt_txt")
    val dtTxt: String,
    @SerializedName("main")
    val main: Main,
    @SerializedName("weather")
    val weather: List<Weather>
)

/**
 * City information
 *
 * API response from "https://openweathermap.org/"
 *
 * @property country
 * @property name name of the city
 */
data class City(
    @SerializedName("country")
    val country: String,
    @SerializedName("name")
    val name: String
)

/**
 * API response from "https://openweathermap.org/"
 *
 * @property temp temperature in Kelvin
 */
data class Main(
    @SerializedName("temp")
    val temp: Double
)

/**
 * API response from "https://openweathermap.org/"
 *
 * @property conditionName description of the weather condition
 * @property icon
 */
data class Weather(
    @SerializedName("description")
    val conditionName: String,
    @SerializedName("icon")
    val icon: String
)