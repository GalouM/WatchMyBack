package com.galou.watchmyback.utils

import androidx.work.*
import com.galou.watchmyback.backgroundWork.CheckUpWorker
import com.galou.watchmyback.data.entity.TripWithData
import java.util.concurrent.TimeUnit

/**
 * @author galou
 * 2019-12-27
 */

fun createCheckUpWorker(trip: TripWithData): PeriodicWorkRequest{
    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .setRequiredNetworkType(NetworkType.CONNECTED).build()
    val frequencyUpdate = trip.trip.updateFrequency?.frequencyMillisecond ?: throw Exception("No frequency setup for $trip")
    return PeriodicWorkRequestBuilder<CheckUpWorker>(frequencyUpdate, TimeUnit.MILLISECONDS)
        .setConstraints(constraints)
        .setInputData(Data.Builder().putString(USER_ID_DATA, trip.trip.userId).build())
        .setInitialDelay(frequencyUpdate, TimeUnit.MILLISECONDS)
        .addTag(CHECK_UP_WORKER_TAG)
        .build()

}