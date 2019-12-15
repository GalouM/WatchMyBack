package com.galou.watchmyback.data.api.reponse


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingApiResponse
    (
    @Json(name = "results")
    val results: List<Result>
)

@JsonClass(generateAdapter = true)
data class Location(
    @Json(name = "adminArea1")
    val countryCode: String,
    @Json(name = "adminArea5")
    val city: String
)

@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "locations")
    val locations: List<Location>
)