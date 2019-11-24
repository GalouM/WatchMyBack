package com.galou.watchmyback.di

import androidx.room.Room
import com.galou.watchmyback.addFriend.AddFriendViewModel
import com.galou.watchmyback.addModifyCheckList.AddModifyCheckListViewModel
import com.galou.watchmyback.checklist.CheckListViewModel
import com.galou.watchmyback.data.repository.*
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.source.local.CheckListLocalDataSource
import com.galou.watchmyback.data.source.local.FriendLocalDataSource
import com.galou.watchmyback.data.source.local.UserLocalDataSource
import com.galou.watchmyback.data.source.remote.CheckListRemoteDataSource
import com.galou.watchmyback.data.source.remote.FriendRemoteDataSource
import com.galou.watchmyback.data.source.remote.UserRemoteDataSource
import com.galou.watchmyback.friends.FriendsViewModel
import com.galou.watchmyback.main.MainActivityViewModel
import com.galou.watchmyback.profile.ProfileViewModel
import com.galou.watchmyback.settings.SettingsViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin [module]
 *
 * Take care of injecting the proper class to all the ViewModels
 */

val appModules = module {

    //firebase
    single {
        FirebaseFirestore.getInstance().apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build()
        }
    }
    single { FirebaseStorage.getInstance() }

    single {
        Room.databaseBuilder(
            androidApplication(),
            WatchMyBackDatabase::class.java,
            "watchMyBack_db_test3.db"
        )
        .build()
    }
    // User sources
    single { UserLocalDataSource(
        userDao = get<WatchMyBackDatabase>().userDao(),
        userPreferencesDao = get<WatchMyBackDatabase>().userPreferencesDao()
    ) }
    single { UserRemoteDataSource(remoteDB = get(), remoteStorage = get()) }
    // Friends sources
    single {
        FriendLocalDataSource(friendDao = get<WatchMyBackDatabase>().friendDao())
    }
    single { FriendRemoteDataSource(remoteDB = get()) }
    // checklists sources
    single {
        CheckListLocalDataSource(checkListDao = get<WatchMyBackDatabase>().checkListDao())
    }
    single {
        CheckListRemoteDataSource(remoteDB = get())
    }

    // Repos
    single<UserRepository> {
        UserRepositoryImpl(userLocalSource = get(), userRemoteSource = get(), friendRemoteSource = get())
    }
    single<FriendRepository> {
        FriendRepositoryImpl(localSource = get(), remoteSource = get())
    }
    single<CheckListRepository> {
        CheckListRepositoryImpl(localSource = get(), remoteSource = get())
    }
    // ViewModels
    viewModel { MainActivityViewModel(userRepository = get()) }
    viewModel { ProfileViewModel(userRepository = get()) }
    viewModel { SettingsViewModel(userRepository = get()) }
    viewModel { FriendsViewModel(userRepository = get(), friendRepository = get()) }
    viewModel { AddFriendViewModel(userRepository = get(), friendRepository = get()) }
    viewModel { CheckListViewModel(userRepository = get(), checkListRepository = get()) }
    viewModel { AddModifyCheckListViewModel(checkListRepository = get(), userRepository = get()) }
}
