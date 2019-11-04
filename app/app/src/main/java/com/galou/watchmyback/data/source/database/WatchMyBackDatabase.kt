package com.galou.watchmyback.data.source.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.data.source.local.dao.*

/**
 * Represent the Database of the application
 *
 * Inherit from [RoomDatabase]
 *
 * Contain all the [Dao]
 *
 * Contain a companion object to create a single instance the database
 *
 * @see Database
 * @see RoomDatabase
 * @see TypeConverters
 * @see Dao
 *
 */
@Database(
    entities = [
        User::class, Trip::class, PointTrip::class, Location::class, UserPreferences::class,
        WeatherData::class, CheckList::class, ItemCheckList::class, Friend::class, Watcher::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WatchMyBackDatabase : RoomDatabase() {

    // --- DAO
    abstract fun checkListDao(): CheckListDao
    abstract fun friendDao(): FriendDao
    abstract fun itemCheckListDao(): ItemCheckListDao
    abstract fun userDao(): UserDao
    abstract fun watcherDao(): WatcherDao
    abstract fun weatherDataDao(): WeatherDataDao
    abstract fun locationDao(): LocationDao
    abstract fun pointTripDao(): PointTripDao
    abstract fun tripDao(): TripDao
    abstract fun userPreferencesDao(): UserPreferencesDao

}