package com.galou.watchmyback.di

import androidx.room.Room
import com.galou.watchmyback.data.repository.FriendRepository
import com.galou.watchmyback.data.repository.FriendRepositoryImpl
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.data.repository.UserRepositoryImpl
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.source.local.FriendLocalDataSource
import com.galou.watchmyback.data.source.local.UserLocalDataSource
import com.galou.watchmyback.data.source.remote.FriendRemoteDataSource
import com.galou.watchmyback.data.source.remote.UserRemoteDataSource
import com.galou.watchmyback.friends.FriendsViewModel
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
    // User sources
    single { UserLocalDataSource(
        userDao = get<WatchMyBackDatabase>().userDao(),
        userPreferencesDao = get<WatchMyBackDatabase>().userPreferencesDao()
    ) }
    single { UserRemoteDataSource() }
    // Friends sources
    single {
        FriendLocalDataSource(friendDao = get<WatchMyBackDatabase>().friendDao())
    }
    single { FriendRemoteDataSource() }

    // Repos
    single<UserRepository> {
        UserRepositoryImpl(localSource = get(), remoteSource = get())
    }
    single<FriendRepository> {
        FriendRepositoryImpl(localSource = get(), remoteSource = get())
    }
    // ViewModels
    viewModel { MainActivityViewModel(userRepository = get()) }
    viewModel { ProfileViewModel(userRepository = get()) }
    viewModel { SettingsViewModel(userRepository = get()) }
    viewModel { FriendsViewModel(userRepository = get(), friendRepository = get()) }
}
