package edu.arizona.cast.austinramsay.glucosemonitor.database

import androidx.room.TypeConverter
import java.util.*

/*
 * These type converters are used for the AndroidX Rooms feature to store and retrieve database
 * values. Only primitive data types can be stored/retrieved, so we need a way to convert more
 * advanced structures into simpler storage.
 */
class GlucoseTypeConverters {

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let { Date(it) }
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

}