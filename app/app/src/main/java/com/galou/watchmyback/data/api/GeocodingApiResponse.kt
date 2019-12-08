package com.galou.watchmyback.data.api


import com.google.gson.annotations.SerializedName

data class GeocodingApiResponse(
    @SerializedName("results")
    val results: List<ResultGeocoding>
)