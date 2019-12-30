package com.galou.watchmyback.utils

import androidx.work.*
import com.galou.watchmyback.backgroundWork.BackHomeWorker
import com.galou.watchmyback.backgroundWork.CheckUpWorker
import com.galou.watchmyback.backgroundWork.LateTripWorker
import com.galou.watchmyback.data.entity.Trip
import java.util.concurrent.TimeUnit

/**
 * @author galou
 * 2019-12-27
 */

fun createCheckUpWorker(trip: Trip): PeriodicWorkRequest{
    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .setRequiredNetworkType(NetworkType.CONNECTED).build()
    val frequencyUpdate = trip.updateFrequency?.frequencyMillisecond ?: throw Exception("No frequency setup for $trip")
    return PeriodicWorkRequestBuilder<CheckUpWorker>(frequencyUpdate, TimeUnit.MILLISECONDS)
        .setConstraints(constraints)
        .setInputData(Data.Builder().putString(USER_ID_DATA, trip.userId).build())
        .setInitialDelay(frequencyUpdate, TimeUnit.MILLISECONDS)
        .addTag(CHECK_UP_WORKER_TAG)
        .build()

}

fun createLateNotificationWorker(userId: String): PeriodicWorkRequest {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED).build()
    return PeriodicWorkRequestBuilder<LateTripWorker>(1, TimeUnit.HOURS)
        .setConstraints(constraints)
        .setInputData(Data.Builder().putString(USER_ID_DATA, userId).build())
        .addTag(LATE_NOTIFICATION_WORKER_TAG)
        .build()
}

fun createBackNotificationWorker(userId: String): PeriodicWorkRequest {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED).build()
    return PeriodicWorkRequestBuilder<BackHomeWorker>(1, TimeUnit.HOURS)
        .setConstraints(constraints)
        .setInputData(Data.Builder().putString(USER_ID_DATA, userId).build())
        .addTag(BACK_NOTIFICATION_WORKER_TAG)
        .build()
}