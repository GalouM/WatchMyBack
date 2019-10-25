package com.galou.watchmyback.di

import androidx.room.Room
import com.galou.watchmyback.data.database.WatchMyBackDatabase
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.mainActivity.MainActivityViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin [module]
 *
 * Take care of injecting the proper class to all the ViewModels
 */

val appModules = module {

    single { UserRepository() }
    viewModel { MainActivityViewModel(userRepository = get()) }
}