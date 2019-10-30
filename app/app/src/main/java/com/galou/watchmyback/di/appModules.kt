package com.galou.watchmyback.di

import androidx.room.Room
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.data.repository.UserRepositoryImpl
import com.galou.watchmyback.data.source.local.UserLocalDataSource
import com.galou.watchmyback.data.source.remote.UserRemoteDataSource
import com.galou.watchmyback.mainActivity.MainActivityViewModel
import com.galou.watchmyback.profileActivity.ProfileViewModel
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
    single { UserLocalDataSource(userDao = get<WatchMyBackDatabase>().userDao()) }
    single { UserRemoteDataSource() }
    single<UserRepository> {
        UserRepositoryImpl(localSource = get(), remoteSource = get())
    }
    viewModel { MainActivityViewModel(userRepository = get()) }
    viewModel { ProfileViewModel(userRepository = get()) }
}
