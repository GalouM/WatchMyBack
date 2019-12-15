package com.galou.watchmyback.data.api

import com.galou.watchmyback.data.api.reponse.GeocodingApiResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author galou
 * 2019-12-07
 */

interface GeocodingApiService {

    @GET("geocoding/v1/reverse?thumbMaps=false&outFormat=json")
    suspend fun getAddressFromLocation(
        @Query("location") location: String,
        @Query("key") apiKey: String
    ): Response<GeocodingApiResponse>

    companion object {
        private const val baseUrl = "http://open.mapquestapi.com/"

        fun create(): GeocodingApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            return retrofit.create(GeocodingApiService::class.java)
        }
    }
}