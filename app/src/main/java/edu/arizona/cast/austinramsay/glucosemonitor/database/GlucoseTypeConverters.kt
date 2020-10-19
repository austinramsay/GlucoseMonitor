package edu.arizona.cast.austinramsay.glucosemonitor.database

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class GlucoseTypeConverters {

    @TypeConverter
    fun fromDate(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun toDate(date: Long): LocalDateTime? {
        return LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC)
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