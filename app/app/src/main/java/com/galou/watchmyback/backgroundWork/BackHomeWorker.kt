package com.galou.watchmyback.backgroundWork

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.NotificationEmittedSaver
import com.galou.watchmyback.data.entity.TripStatus
import com.galou.watchmyback.data.entity.TripWithData
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.CHANNEL_BACK_ID
import com.galou.watchmyback.utils.Result.*
import com.galou.watchmyback.utils.USER_ID_DATA
import com.galou.watchmyback.utils.extension.showNotification
import com.galou.watchmyback.utils.extension.updateStatus
import kotlinx.coroutines.coroutineScope
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * @author galou
 * 2019-12-28
 */
class BackHomeWorker (appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val tripRepository: TripRepository by inject()
    private val userRepository: UserRepository by inject()

    private lateinit var userId: String
    private lateinit var userPreferences: UserPreferences

    override suspend fun doWork(): Result = coroutineScope {
        with(inputData.getString(USER_ID_DATA)){
            if (this != null){
                userId = this
                fetchUserPrefs()
            } else Result.failure()
        }


    }

    private suspend fun fetchUserPrefs(): Result {
        when (val userPrefsResult = userRepository.fetchUserPreferences(userId)) {
            is Success -> {
                userPrefsResult.data?.let {
                    userPreferences = userPrefsResult.data
                    return fetchTripWatching()
                }
            }

        }
        return Result.failure()
    }

    private suspend fun fetchTripWatching(): Result{
        when (val trips = tripRepository.fetchTripUserWatching(userId)) {
            is Success -> {
                trips.data.forEach {
                    it.updateStatus()
                    fetchNotificationEmitted(it)
                }
                return Result.success()
            }
        }
        return Result.failure()
    }

    private suspend fun fetchTripOwnerName(trip: TripWithData){
        when (val tripOwner = userRepository.fetchTripOwner(trip.trip.userId)) {
            is Success -> {
                if (trip.trip.status == TripStatus.BACK_SAFE){
                    emitNotification(trip, tripOwner.data.username!!)
                }
            }
        }
    }

    private suspend fun fetchNotificationEmitted(trip: TripWithData){
        when (val notificationEmittedSaverResult = tripRepository.fetchNotificationEmittedSaver(userId, trip.trip.id)){
            is Success -> {
                var notificationEmittedSaver = notificationEmittedSaverResult.data
                if (notificationEmittedSaver == null){
                    when(val task = createAndGetNotificationEmitted(trip)){
                        is Success -> notificationEmittedSaver = task.data
                    }
                }
                if (!notificationEmittedSaver!!.backSafeNotificationEmitted){
                    notificationEmittedSaver.backSafeNotificationEmitted = true
                    tripRepository.updateNotificationSaver(notificationEmittedSaver)
                    fetchTripOwnerName(trip)
                }

            }
        }
    }

    private suspend fun createAndGetNotificationEmitted(trip: TripWithData): com.galou.watchmyback.utils.Result<NotificationEmittedSaver?>{
        return when(val creationTask = tripRepository.createNotificationSaver(trip.trip.id, userId)){
            is Success -> tripRepository.fetchNotificationEmittedSaver(userId, trip.trip.id)
            is Error -> Error(
                creationTask.exception
            )
            is Canceled -> Canceled(
                creationTask.exception
            )
        }
    }

    private fun emitNotification(trip: TripWithData, ownerName: String){
        val title = applicationContext.getString(R.string.back_home_notification_title, ownerName)
        val description = ""

        applicationContext.showNotification(description, title, CHANNEL_BACK_ID, trip.trip.type!!.iconId)

    }
}