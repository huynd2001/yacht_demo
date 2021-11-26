package com.csds393.yacht.weather

import androidx.room.Embedded
import androidx.room.Entity
import com.csds393.yacht.database.DB
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.net.*
import java.time.*
import java.util.regex.Pattern

/** Will eventually become fleshed-out Weather Component of back-end */
object Weather {

    private val json = Json { ignoreUnknownKeys = true }

    private fun retrievableWeatherDays() = LocalDate.now()..(LocalDate.now().plusDays(6))


    fun getForecast(latitude: Double, longitude: Double): List<DayWeather> {
        if (true) return retrieveForecastsForCoordinates(latitude, longitude)!!
        val window = retrievableWeatherDays()

        val weatherDao = DB.getInstance().weatherDao
//         check db for entry of day
        val dbResult = weatherDao._getWeather(window.start, window.endInclusive)
        // if absent and internet available, fetch

        val missingDates = setOf(window).minus(dbResult.keys.toList())
        // TODO: 11/18/2021 convert range to set, check whether missing set contains any from retrievable

//        if (missingDates.contains(retrievableWeatherDays()))
        // TODO: 11/18/2021 check for internet first

        // if present and internet available, check if expired
            // if so, update DB
        // return contents of DB
        val retrievedForecasts =  try {
            val rf = retrieveForecastsForCoordinates(latitude, longitude)
            rf?.let {
                weatherDao.insertForecasts(rf)
            }
            rf
        } catch (e: Exception) {
            emptyList()
        }
        return retrievedForecasts?: emptyList()
    }

    /** Prototype for Weather Component procedure of polling NWS and decoding JSON */
    private fun retrieveForecastsForCoordinates(latitude: Double, longitude: Double): List<DayWeather>? {
        val forecastURL = retrieveForecastURLFromPointsURL(composePointsURL(latitude, longitude), userAgent)
        val forecastJSON = forecastURL?.let { getResponseFromURL(it, userAgent) }
        val element = forecastJSON?.let { json.parseToJsonElement(it) }
        val properties = element?.jsonObject?.get("properties")?.jsonObject

        // if properties not null, then internal fields can be assumed nonnull
        return properties?.let {
            val dateRetrievedString = properties["updated"]!!.jsonPrimitive.content
            val dateTimeUpdated: ZonedDateTime = ZonedDateTime.parse(dateRetrievedString)

            val weatherElementList = properties["periods"]!!.jsonArray
            val jsonHalfDayWeatherList = weatherElementList
                .map<JsonElement, JsonHalfDayWeather> { json.decodeFromJsonElement(it) }

            val dayWeatherList = mutableListOf<DayWeather>()
            val iter = jsonHalfDayWeatherList.listIterator()

            while (iter.hasNext()) {
                // get pairs by date
                val morningWeather = iter.next()
                val eveningWeather = iter.next()
                // merge
                dayWeatherList.add(
                    DayWeather(
                        morningWeather.zonedDateTime.toLocalDate(),
                        dateTimeUpdated,
                        morningWeather.toHalfDayWeather(),
                        eveningWeather.toHalfDayWeather(),
                    )
                )
            }
            return dayWeatherList
        }

    }

    val userAgent = "YACHT calendar app"

    private fun composePointsURL(latitude: Double, longitude: Double) =
        URL(String.format("https://api.weather.gov/points/%.3f,%.3f", latitude,longitude))

    private fun retrieveForecastURLFromPointsURL(pointsURL: URL, userAgent: String): URL? {
        val jsonString = getResponseFromURL(pointsURL, userAgent)
        val element = json.parseToJsonElement(jsonString)
        val forecastURLString = element.jsonObject["properties"]?.jsonObject?.get("forecast")?.jsonPrimitive?.content
        return forecastURLString?.let { URL(it) }
    }

    private fun getResponseFromURL(forecastUrl: URL, userAgent: String): String {
        val string = (forecastUrl.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            setRequestProperty("User-Agent", userAgent)
            requestMethod = "GET"
            doInput = true
            inputStream.use {
                it.bufferedReader().readText()
            }
        }
        return string!!
    }

}

/**
 * Object that directly maps to period object from GeoJSON.
 * Not intended for use outside of Weather component.
 * See HalfDayWeather
 */
@Serializable
private data class JsonHalfDayWeather(
    @SerialName("startTime")
    private val zonedDateTimeString: String,
    val isDaytime: Boolean,
    val temperature: Int,
    private val windSpeed: String,
    val shortForecast: String,
    val detailedForecast: String,
) {
    val windSpeedRange: IntRange by lazy {
        val matcher = numberPattern.matcher(windSpeed)
        matcher.find()
        val start = Integer.parseInt(matcher.group())
        val end = if (matcher.find()) Integer.parseInt(matcher.group()) else start
        IntRange(start, end)
    }
    val zonedDateTime: ZonedDateTime by lazy { ZonedDateTime.parse(zonedDateTimeString) }

    // TODO: 10/25/2021  more sophisticated sky field and parsing
    private val sky: Sky by lazy {
        when {
            shortForecast.contains("Sunny") -> Sky.SUNNY
            shortForecast.contains("Cloudy") -> Sky.CLOUDY
            shortForecast.contains("Rain") -> Sky.RAINY
            else -> Sky.CLEAR
        }
    }

    fun toHalfDayWeather() =
        HalfDayWeather(
            temperature,
            windSpeedRange,
            sky,
            shortForecast,
            detailedForecast,
        )

    companion object {
        private val numberPattern: Pattern = Pattern.compile("\\d+")
    }
}

@Entity(
    tableName = "daily_forecasts",
    primaryKeys = ["date"]
)
data class DayWeather(
    val date: LocalDate,
    val dateRetrieved: ZonedDateTime,
    @Embedded(prefix = "day")
    val morningWeather: HalfDayWeather,
    @Embedded(prefix = "night")
    val nightWeather: HalfDayWeather,
)

data class HalfDayWeather(
    val temperature: Int,
    val windSpeed: IntRange,
    val sky: Sky,
    val shortForecast: String,
    val detailedForecast: String,
)

// TODO: 10/24/2021  refactor, perhaps compose Forecast from Likelihood+Condition + optional then Forecast
enum class Sky {
    SUNNY, CLOUDY, RAINY, CLEAR
}
