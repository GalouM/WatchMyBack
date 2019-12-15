package com.galou.watchmyback.data.api

import com.galou.watchmyback.data.api.reponse.OpenWeatherMapApiResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author galou
 * 2019-12-08
 */

interface OpenWeatherService {

    @GET("data/2.5/weather?mode=JSON")
    suspend fun getWeatherLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("APPID") apiKey: String
    ): Response<OpenWeatherMapApiResponse>

    companion object {

        private const val baseUrl = "http://api.openweathermap.org/"

        fun create(): OpenWeatherService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            return retrofit.create(OpenWeatherService::class.java)
        }
    }

}