package com.galou.watchmyback.data.api

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author galou
 * 2019-12-07
 */

const val geocodingApiBaseUrl = "http://open.mapquestapi.com"

interface GeocodingApiService {

    @GET("geocoding/v1/reverse?thumbMaps=false&outFormat=json")
    fun getAddressFromLocation(
        @Query("location") location: String,
        @Query("key") apiKey: String
    ): Observable<GeocodingApiResponse>

    companion object {
        fun create(): GeocodingApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(geocodingApiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            return retrofit.create(GeocodingApiService::class.java)
        }
    }
}