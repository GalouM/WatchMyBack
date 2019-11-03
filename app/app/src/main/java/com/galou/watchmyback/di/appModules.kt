package com.galou.watchmyback.di

import androidx.room.Room
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.data.repository.UserRepositoryImpl
import com.galou.watchmyback.data.source.local.UserLocalDataSource
import com.galou.watchmyback.data.source.remote.UserRemoteDataSource
import com.galou.watchmyback.main.MainActivityViewModel
import com.galou.watchmyback.profile.ProfileViewModel
import com.galou.watchmyback.settings.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin [module]
 *
 * Take care of injecting the proper class to all the ViewModels
 */

val appModules = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            WatchMyBackDatabase::class.java,
            "watchMyBack_db_test.db"
        )
        .build()
    }
    single { UserLocalDataSource(
        userDao = get<WatchMyBackDatabase>().userDao(),
        userPreferencesDao = get<WatchMyBackDatabase>().userPreferencesDao()
    ) }
    single { UserRemoteDataSource() }
    single<UserRepository> {
        UserRepositoryImpl(localSource = get(), remoteSource = get())
    }
    viewModel { MainActivityViewModel(userRepository = get()) }
    viewModel { ProfileViewModel(userRepository = get()) }
    viewModel { SettingsViewModel(userRepository = get()) }
}
