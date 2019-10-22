package com.galou.watchmyback.data.database

import androidx.room.TypeConverter
import com.galou.watchmyback.data.entity.*
import java.util.*

/**
 * Converter for Room DB
 *
 * Convert enum and Date to data Room can understand
 *
 * @see TypeConverter
 *
 * @author Galou Minisini
 *
 */
class Converters {
    companion object {

        @TypeConverter
        @JvmStatic
        fun fromTypePoint(type: TypePoint) = type.typeName

        @TypeConverter
        @JvmStatic
        fun toTypePoint(type: String): TypePoint {
            TypePoint.values().forEach {
                if(type == it.typeName) return it
            }

            throw Exception("Type of Point not recognize")
        }

        @TypeConverter
        @JvmStatic
        fun fromTripType(type: TripType) = type.typeNameId

        @TypeConverter
        @JvmStatic
        fun toTripType(type: Int): TripType {
            TripType.values().forEach {
                if(type == it.typeNameId) return it
            }

            throw Exception("Type of Trip not recognize")
        }

        @TypeConverter
        @JvmStatic
        fun fromTripUpdateFrequency(type: TripUpdateFrequency) = type.frequencyMillisecond

        @TypeConverter
        @JvmStatic
        fun toTripUpdateFrequency(type: Long): TripUpdateFrequency {
            TripUpdateFrequency.values().forEach {
                if(type == it.frequencyMillisecond) return it
            }

            throw Exception("Update Frequency not recognize")
        }

        @TypeConverter
        @JvmStatic
        fun fromTripStatus(type: TripStatus) = type.statusNameId

        @TypeConverter
        @JvmStatic
        fun toTripStatus(type: Int): TripStatus {
            TripStatus.values().forEach {
                if(type == it.statusNameId) return it
            }

            throw Exception("Trip Status not recognize")
        }

        @TypeConverter
        @JvmStatic
        fun fromWeatherCondition(type: WeatherCondition) = type.conditionNameId

        @TypeConverter
        @JvmStatic
        fun toWeatherCondition(type: Int): WeatherCondition {
            WeatherCondition.values().forEach {
                if(type == it.conditionNameId) return it
            }

            throw Exception("Weather Condition not recognize")
        }


        @TypeConverter
        @JvmStatic
        fun fromTimeStamp(value: Long?): Date? {
            value?.let{
                return Date(value)
            }
            return null

        }

        @TypeConverter
        @JvmStatic
        fun dateToTimeStamp(date: Date?): Long? {
            date?.let{
                return date.time
            }

            return null
        }
    }
}