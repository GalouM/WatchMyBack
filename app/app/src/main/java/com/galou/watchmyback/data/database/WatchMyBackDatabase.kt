package com.galou.watchmyback.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.galou.watchmyback.data.database.dao.*
import com.galou.watchmyback.data.entity.*

/**
 * Created by galou on 2019-10-21
 */
@Database(
    entities = [
        User::class, Trip::class, PointTrip::class, Location::class,
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

    companion object{
        @Volatile
        private var INSTANCE: WatchMyBackDatabase? = null
        fun getDatabase(context: Context): WatchMyBackDatabase {
            return INSTANCE
                ?: synchronized(this){
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        WatchMyBackDatabase::class.java,
                        "WatchMyBack_database.db")
                        .build()
                    INSTANCE = instance
                    return instance
                }
        }
    }
}