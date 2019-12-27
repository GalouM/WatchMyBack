package com.galou.watchmyback.backgroundWork

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.data.repository.TripRepository
import com.galou.watchmyback.utils.Result.Success
import com.galou.watchmyback.utils.USER_ID_DATA
import com.galou.watchmyback.utils.await
import com.galou.watchmyback.utils.extension.isGPSEnabled
import com.galou.watchmyback.utils.extension.isLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.coroutineScope
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * @author galou
 * 2019-12-26
 */
class CheckUpWorker(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams), KoinComponent {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val tripRepository: TripRepository by inject()

    override suspend fun doWork(): Result = coroutineScope {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        val userId = inputData.getString(USER_ID_DATA)

        if (userId != null && applicationContext.isGPSEnabled() && applicationContext.isLocationPermission()) {
            when(val location = fusedLocationClient.lastLocation.await()){
                is Success -> {
                    if (location.data != null){
                        val coordinate = Coordinate(location.data.latitude, location.data.longitude)
                        when(tripRepository.createCheckUpPoint(userId, coordinate)){
                            is Success -> Result.success()

                            else -> Result.failure()
                        }
                    } else Result.failure()

                }
                else -> Result.failure()
            }
        } else Result.failure()



    }


}