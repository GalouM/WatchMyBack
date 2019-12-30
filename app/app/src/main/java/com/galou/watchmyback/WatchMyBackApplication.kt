package com.galou.watchmyback

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.galou.watchmyback.di.appModules
import com.galou.watchmyback.utils.CHANNEL_BACK_ID
import com.galou.watchmyback.utils.CHANNEL_LATE_ID
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * An [Application] that use Koin for dependency injection
 *
 * @author Galou Minisini
 *
 */
class WatchMyBackApplication : Application() {

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationLateChannel()
        createNotificationBackLateChannel()

        startKoin {
            androidLogger()
            androidContext(this@WatchMyBackApplication)
            modules(appModules)
        }
    }

    private fun createNotificationLateChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(R.string.channel_late_name)
            val descriptionText = getString(R.string.channel_late_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_LATE_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationBackLateChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(R.string.channel_back_name)
            val descriptionText = getString(R.string.channel_back_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_BACK_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}