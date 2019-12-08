package com.galou.watchmyback.data.api


import com.google.gson.annotations.SerializedName

data class ResultGeocoding(
    @SerializedName("locations")
    val locations: List<LocationGeocoding>
)