package com.galou.watchmyback.di

import androidx.room.Room
import com.galou.watchmyback.addFriend.AddFriendViewModel
import com.galou.watchmyback.addModifyCheckList.AddModifyCheckListViewModel
import com.galou.watchmyback.addTrip.AddTripViewModel
import com.galou.watchmyback.checklist.CheckListViewModel
import com.galou.watchmyback.data.api.GeocodingApiService
import com.galou.watchmyback.data.api.OpenWeatherService
import com.galou.watchmyback.data.repository.*
import com.galou.watchmyback.data.source.database.WatchMyBackDatabase
import com.galou.watchmyback.data.source.local.CheckListLocalDataSource
import com.galou.watchmyback.data.source.local.FriendLocalDataSource
import com.galou.watchmyback.data.source.local.TripLocalDataSource
import com.galou.watchmyback.data.source.local.UserLocalDataSource
import com.galou.watchmyback.data.source.remote.CheckListRemoteDataSource
import com.galou.watchmyback.data.source.remote.FriendRemoteDataSource
import com.galou.watchmyback.data.source.remote.TripRemoteDataSource
import com.galou.watchmyback.data.source.remote.UserRemoteDataSource
import com.galou.watchmyback.detailsPoint.DetailsPointViewModel
import com.galou.watchmyback.detailsTrip.DetailsTripViewModel
import com.galou.watchmyback.friends.FriendsViewModel
import com.galou.watchmyback.main.MainActivityViewModel
import com.galou.watchmyback.mapPickLocation.PickLocationMapViewModel
import com.galou.watchmyback.profile.ProfileViewModel
import com.galou.watchmyback.settings.SettingsViewModel
import com.galou.watchmyback.tripMapView.TripMapViewModel
import com.galou.watchmyback.tripsView.TripsViewModel
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
            "watchMyBack_db_test12.db"
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
    // Trip sources
    single { TripLocalDataSource(tripDao = get<WatchMyBackDatabase>().tripDao()) }
    single { TripRemoteDataSource(remoteDB = get()) }

    // API Services
    single { GeocodingApiService.create() }

    single { OpenWeatherService.create() }

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

    single<TripRepository> {
        TripRepositoryImpl(
            geocodingApiService = get(),
            weatherService = get(),
            localSource = get(),
            remoteSource = get()
        )
    }

    // ViewModels
    viewModel { MainActivityViewModel(userRepository = get()) }
    viewModel { ProfileViewModel(userRepository = get()) }
    viewModel { SettingsViewModel(userRepository = get()) }
    viewModel { FriendsViewModel(userRepository = get(), friendRepository = get()) }
    viewModel { AddFriendViewModel(userRepository = get(), friendRepository = get()) }
    viewModel { CheckListViewModel(userRepository = get(), checkListRepository = get()) }
    viewModel { AddModifyCheckListViewModel(checkListRepository = get(), userRepository = get()) }
    viewModel { TripMapViewModel(tripRepository = get(), userRepository = get()) }
    viewModel { AddTripViewModel(
        friendRepository = get(),
        userRepository = get(),
        checkListRepository = get(),
        tripRepository = get()) }
    viewModel { PickLocationMapViewModel(tripRepository = get()) }
    viewModel { DetailsPointViewModel(tripRepository = get(), userRepository = get()) }
    viewModel { DetailsTripViewModel(
        tripRepository = get(),
        userRepository = get(),
        checkListRepository = get()
    ) }
    viewModel { TripsViewModel() }
}
