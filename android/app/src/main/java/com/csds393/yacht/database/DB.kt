package com.csds393.yacht.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.csds393.yacht.calendar.CalendarDao
import com.csds393.yacht.calendar.CalendarEvent
import com.csds393.yacht.calendar.DatePattern
import com.csds393.yacht.calendar.RecurringCalendarEvent
import com.csds393.yacht.weather.DayWeather
import com.csds393.yacht.weather.HalfDayWeather
import com.csds393.yacht.weather.WeatherDao

/**
 * SQL Database that stores Calendar data and Weather forecasts
 */
@Database(
    entities = [
        CalendarEvent::class,
        RecurringCalendarEvent::class,
        RecurringCalendarEvent.Exception::class,
        DayWeather::class,
               ],
    version = 5,
    exportSchema = false)
@TypeConverters(Converters::class, DatePattern::class)
abstract class DB : RoomDatabase() {

    /** Methods for manipulating the Calendar. Defined in Calendar package */
    abstract val calendarDao: CalendarDao

    /** Methods for retrieving forecasts. Defined in Weather package */
    abstract val weatherDao: WeatherDao

    companion object {
        @Volatile private var INSTANCE: DB? = null

        fun initializeDB(context: Context) {
            INSTANCE ?: buildDatabase(context)
        }

        /** Must have called initializeDB prior */
        fun getInstance() = INSTANCE!!

        /** Initializes if necessary, returns DB */
        fun getInstance(context: Context): DB = synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        // TODO: 11/18/2021  separate initialize from retrieve. so that other components can access DB

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, DB::class.java, "yacht.db")
                .fallbackToDestructiveMigration()
                .build()

    }
}
