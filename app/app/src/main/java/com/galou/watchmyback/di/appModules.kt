package com.galou.watchmyback.di

import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.data.repository.UserRepositoryImpl
import com.galou.watchmyback.mainActivity.MainActivityViewModel
import com.galou.watchmyback.profileActivity.ProfileViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin [module]
 *
 * Take care of injecting the proper class to all the ViewModels
 */

val appModules = module {

    single<UserRepository> { UserRepositoryImpl() }
    viewModel { MainActivityViewModel(userRepository = get()) }
    viewModel { ProfileViewModel(userRepository = get()) }
}
