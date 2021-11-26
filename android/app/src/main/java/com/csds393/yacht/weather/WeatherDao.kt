package com.csds393.yacht.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import java.time.LocalDate
import java.time.ZonedDateTime

@Dao
interface WeatherDao {

    /* Create */

    @Insert(onConflict = REPLACE)
    fun insertForecast(dayWeather: DayWeather)

    @Insert(onConflict = REPLACE)
    fun insertForecasts(dayWeathers: List<DayWeather>)


    /* Read */

    @Query("SELECT * FROM daily_forecasts WHERE :earliest <= date <= :latest GROUP BY date")
    fun _getWeather(earliest: LocalDate, latest: LocalDate): Map<LocalDate, DayWeather> = mapOf()

    @Query("SELECT dateRetrieved FROM daily_forecasts WHERE date == :date")
    fun _getDateRetrievedForForecast(date: LocalDate): List<ZonedDateTime>


    /* Delete */

    @Query("DELETE FROM daily_forecasts")
    fun clearAllForecasts()
}
