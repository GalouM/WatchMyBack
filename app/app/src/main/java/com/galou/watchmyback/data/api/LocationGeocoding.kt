package com.galou.watchmyback.data.api


import com.google.gson.annotations.SerializedName

data class LocationGeocoding(
    @SerializedName("adminArea1")
    val countryCode: String,
    @SerializedName("adminArea5")
    val city: String
)