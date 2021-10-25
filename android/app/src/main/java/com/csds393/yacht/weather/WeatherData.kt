package com.csds393.yacht.weather

import java.time.DayOfWeek
import java.time.DayOfWeek.*

// TODO: 10/21/2021  implement JSON equivalent for https://api.weather.gov/gridpoints/LWX/96,70/forecast
    // TODO: 10/21/2021  determine significance of @Context
    // TODO: 10/21/2021  nested classes for weather
    // TODO: 10/21/2021  map this class to a nicer data class

@Deprecated("Use Weather.HalfDayWeather data class instead. This will be removed after 10/25/2021")
data class WeatherData(
    val dayOfWeek: DayOfWeek,
    val temperature: Int,
    val sky: Sky,
    val windMPH: Int,
    ) {

    // from https://api.weather.gov/gridpoints/CLE/85,65/forecast
    // @ 10/21/2021, 5:29 PM
    // Just has the Day-time weather, not the nighttime
    companion object {
        @JvmStatic
        val mockData: List<WeatherData> = listOf(
            WeatherData(THURSDAY, 78, Sky.SUNNY, 8),
            WeatherData(FRIDAY, 70, Sky.SUNNY, 6),
            WeatherData(SATURDAY, 67, Sky.SUNNY, 4),
            WeatherData(SUNDAY, 71, Sky.SUNNY, 8),
            WeatherData(MONDAY, 68, Sky.RAINY, 6),
            WeatherData(TUESDAY, 68, Sky.RAINY, 8),
            WeatherData(WEDNESDAY, 64, Sky.CLOUDY, 7),
        )
    }
}
