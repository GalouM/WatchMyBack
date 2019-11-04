package com.galou.watchmyback

import android.app.Application
import com.galou.watchmyback.di.appModules
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
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@WatchMyBackApplication)
            modules(appModules)
        }
    }
}