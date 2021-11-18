package com.csds393.yacht.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

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


    }
}