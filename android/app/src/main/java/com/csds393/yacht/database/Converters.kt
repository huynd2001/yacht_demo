package com.csds393.yacht.database

import androidx.room.TypeConverter
import java.time.*

class Converters {

    companion object {
        @TypeConverter
        @JvmStatic
        fun localTimeToLong(value: LocalTime?): Long? = value?.toNanoOfDay()
        @TypeConverter
        @JvmStatic
        fun longToLocalTime(value: Long?): LocalTime? = value?.let { LocalTime.ofNanoOfDay(it) }

        @TypeConverter
        @JvmStatic
        fun localDateToInt(value: LocalDate?): Int? = value?.toEpochDay()?.toInt()
        @TypeConverter
        @JvmStatic
        fun intToLocalDate(value: Int?): LocalDate? = value?.let { LocalDate.ofEpochDay(it.toLong()) }

        @TypeConverter
        @JvmStatic
        fun localDateRangeToLong(value: ClosedRange<LocalDate>?): Long? = value?.let {
            localDateToInt(value.endInclusive)!!.toLong().shl(32) + localDateToInt(value.start)!!
        }
        @TypeConverter
        @JvmStatic
        fun longToLocalDateRange(value: Long?): ClosedRange<LocalDate>? {
            return value?.let {
                intToLocalDate(value.and(0xffff_ffffL).toInt())!!..intToLocalDate(value.ushr(32).toInt())!!
            }
        }

        @TypeConverter
        @JvmStatic
        fun intRangeToLong(value: IntRange?): Long? = value?.let {
            value.last.toLong().shl(32) + value.first
        }
        @TypeConverter
        @JvmStatic
        fun longToIntRange(value: Long?): IntRange? {
            return value?.let {
                value.and(0xFFFF_FFFF).toInt()..(value.ushr(32).and(0xFFFF_FFFF).toInt())
            }
        }

        @TypeConverter
        @JvmStatic
        fun zonedDateTimeToLong(value: ZonedDateTime?): Long? = value?.let {
            value.toEpochSecond()
        }
        @TypeConverter
        @JvmStatic
        fun longToZonedDateTime(value: Long?): ZonedDateTime? {
            return value?.let {
                LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.ofHours(0)).atZone(ZoneId.systemDefault())
            }
        }

    }
}