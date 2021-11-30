package com.csds393.yacht.weather

import androidx.room.Embedded
import androidx.room.Entity
import com.csds393.yacht.database.DB
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.net.*
import java.time.*
import java.util.concurrent.Executors
import java.util.regex.Pattern
import kotlin.math.round

/** Will eventually become fleshed-out Weather Component of back-end */
object Weather {

    private val json = Json { ignoreUnknownKeys = true }

    private val weatherFetcher = Executors.newSingleThreadExecutor()

    /*
     One degree (of latitude) is about 85 km.
     Want the broadest range where a forecast is still applicable
     1km seems reasonable
     So 85km/128 seems reasonable for precision.
     log_2(128) == 7 bits
    */
    /** For Latitude and Longitude, how many bits of precision after the point. */
    private const val COORDINATE_PRECISION_IN_BITS = 7

    private fun Double.roundToNumBits(precisionBits: Int) =
            round(this.times(1 shl precisionBits)).div(1 shl precisionBits)

    /** Given a [latitude] and [longitude], returns all  */
    fun getForecast(latitude: Double, longitude: Double): List<DayWeather> {

        // TODO: 11/28/2021  skip web query if last query was recent enough
        // round coords
        val lat = latitude.roundToNumBits(COORDINATE_PRECISION_IN_BITS)
        val lon = longitude.roundToNumBits(COORDINATE_PRECISION_IN_BITS)

        val weatherDao = DB.getInstance().weatherDao
        // attempt web query, update db
        try { weatherFetcher.submit {
            val rf = retrieveForecastsForCoordinates(lat, lon)
            rf?.let {
                weatherDao.insertForecasts(rf)
            }
        } } catch (e: Exception) {
        }

        // return db results
        return weatherDao.getWeather(lat, lon)
    }

    /** Polls NWS and and parses weather data into DayWeathers. Returns null on failure */
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

            return buildList {
                val iter = ( // if first is not dayTime, need to drop first and last to re-align half-days
                        if (! jsonHalfDayWeatherList.first().isDaytime) jsonHalfDayWeatherList.drop(1).dropLast(1)
                        else jsonHalfDayWeatherList
                        ).listIterator()

                while (iter.hasNext()) {
                    // get pairs by date
                    val morningWeather = iter.next()
                    val eveningWeather = iter.next()
                    // merge
                    add(DayWeather(
                            morningWeather.zonedDateTime.toLocalDate(),
                            latitude,
                            longitude,
                            dateTimeUpdated,
                            morningWeather.toHalfDayWeather(),
                            eveningWeather.toHalfDayWeather(),
                    ))
                }
            }
        }

    }

    private const val userAgent = "YACHT calendar app"

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
            readTimeout = 2000
            connectTimeout = 3000
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

    private val sky: Sky by lazy {
        shortForecast
            .filter { it.isWhitespace() or it.isLetter() }
            .uppercase()
            .split(" ")
            .find { it in skyNames }
            ?.let { Sky.valueOf(it) } ?: Sky.CLEAR
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
    primaryKeys = ["date", "latitude", "longitude"]
)
data class DayWeather(
    val date: LocalDate,
    val latitude: Double,
    val longitude: Double,
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

enum class Sky {
    SNOW, SUNNY, CLOUDY, RAIN, CLEAR
}
val skyNames = Sky.values().map { it.name }
