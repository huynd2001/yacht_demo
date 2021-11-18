package com.csds393.yacht.weather

import androidx.room.Dao
import java.time.LocalDate

@Dao
interface WeatherDao {

    fun getWeather(earliest: LocalDate, latest: LocalDate): Map<LocalDate, DayWeather> = mapOf()
}