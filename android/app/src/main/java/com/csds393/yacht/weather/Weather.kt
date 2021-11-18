package com.csds393.yacht.weather

import androidx.room.Ignore
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.net.*
import java.time.*
import java.util.regex.Pattern

/** Temp stand in for getting from online. */
private const val weatherJSONString =
    "{\n\"@context\": [\n\"https://geojson.org/geojson-ld/geojson-context.jsonld\",\n  {\n\"@version\": \"1.1\",\n\"wx\": \"https://api.weather.gov/ontology#\",\n\"geo\": \"http://www.opengis.net/ont/geosparql#\",\n\"unit\": \"http://codes.wmo.int/common/unit/\",\n\"@vocab\": \"https://api.weather.gov/ontology#\"\n  }\n ],\n\"type\": \"Feature\",\n\"geometry\": {\n\"type\": \"Polygon\",\n\"coordinates\": [\n      [\n          [\n              -77.036996200000004,\n              38.900789000000003\n          ],\n          [\n              -77.040754800000002,\n              38.878836500000006\n          ],\n          [\n              -77.012551900000005,\n              38.875908600000002\n          ],\n          [\n              -77.008787600000005,\n              38.897860800000004\n          ],\n          [\n              -77.036996200000004,\n              38.900789000000003\n          ]\n      ]\n  ]\n },\n\"properties\": {\n\"updated\": \"2021-11-17T23:30:36+00:00\",\n\"units\": \"us\",\n\"forecastGenerator\": \"BaselineForecastGenerator\",\n\"generatedAt\": \"2021-11-17T23:52:57+00:00\",\n\"updateTime\": \"2021-11-17T23:30:36+00:00\",\n\"validTimes\": \"2021-11-17T17:00:00+00:00/P7DT8H\",\n\"elevation\": {\n\"unitCode\": \"wmoUnit:m\",\n\"value\": 6.0960000000000001\n  },\n\"periods\": [\n      {\n \"number\": 1,\n \"name\": \"Tonight\",\n \"startTime\": \"2021-11-17T18:00:00-05:00\",\n \"endTime\": \"2021-11-18T06:00:00-05:00\",\n \"isDaytime\": false,\n \"temperature\": 51,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"3 to 7 mph\",\n \"windDirection\": \"S\",\n \"icon\": \"https://api.weather.gov/icons/land/night/few?size=medium\",\n \"shortForecast\": \"Mostly Clear\",\n \"detailedForecast\": \"Mostly clear, with a low around 51. South wind 3 to 7 mph.\"\n      },\n      {\n \"number\": 2,\n \"name\": \"Thursday\",\n \"startTime\": \"2021-11-18T06:00:00-05:00\",\n \"endTime\": \"2021-11-18T18:00:00-05:00\",\n \"isDaytime\": true,\n \"temperature\": 74,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": \"falling\",\n \"windSpeed\": \"7 to 15 mph\",\n \"windDirection\": \"SW\",\n \"icon\": \"https://api.weather.gov/icons/land/day/few/rain_showers,30?size=medium\",\n \"shortForecast\": \"Sunny then Chance Rain Showers\",\n \"detailedForecast\": \"A chance of rain showers after 4pm. Sunny. High near 74, with temperatures falling to around 68 in the afternoon. Southwest wind 7 to 15 mph, with gusts as high as 30 mph. Chance of precipitation is 30%. New rainfall amounts less than a tenth of an inch possible.\"\n      },\n      {\n \"number\": 3,\n \"name\": \"Thursday Night\",\n \"startTime\": \"2021-11-18T18:00:00-05:00\",\n \"endTime\": \"2021-11-19T06:00:00-05:00\",\n \"isDaytime\": false,\n \"temperature\": 41,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"12 mph\",\n \"windDirection\": \"W\",\n \"icon\": \"https://api.weather.gov/icons/land/night/rain_showers,70/rain_showers,60?size=medium\",\n \"shortForecast\": \"Rain Showers Likely\",\n \"detailedForecast\": \"Rain showers likely. Mostly cloudy, with a low around 41. West wind around 12 mph, with gusts as high as 20 mph. Chance of precipitation is 70%. New rainfall amounts between a tenth and quarter of an inch possible.\"\n      },\n      {\n \"number\": 4,\n \"name\": \"Friday\",\n \"startTime\": \"2021-11-19T06:00:00-05:00\",\n \"endTime\": \"2021-11-19T18:00:00-05:00\",\n \"isDaytime\": true,\n \"temperature\": 50,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"9 to 16 mph\",\n \"windDirection\": \"NW\",\n \"icon\": \"https://api.weather.gov/icons/land/day/rain_showers,20/few?size=medium\",\n \"shortForecast\": \"Slight Chance Rain Showers then Sunny\",\n \"detailedForecast\": \"A slight chance of rain showers before 7am. Sunny, with a high near 50. Northwest wind 9 to 16 mph, with gusts as high as 25 mph. Chance of precipitation is 20%.\"\n      },\n      {\n \"number\": 5,\n \"name\": \"Friday Night\",\n \"startTime\": \"2021-11-19T18:00:00-05:00\",\n \"endTime\": \"2021-11-20T06:00:00-05:00\",\n \"isDaytime\": false,\n \"temperature\": 29,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"2 to 8 mph\",\n \"windDirection\": \"NW\",\n \"icon\": \"https://api.weather.gov/icons/land/night/few?size=medium\",\n \"shortForecast\": \"Mostly Clear\",\n \"detailedForecast\": \"Mostly clear, with a low around 29. Northwest wind 2 to 8 mph.\"\n      },\n      {\n \"number\": 6,\n \"name\": \"Saturday\",\n \"startTime\": \"2021-11-20T06:00:00-05:00\",\n \"endTime\": \"2021-11-20T18:00:00-05:00\",\n \"isDaytime\": true,\n \"temperature\": 50,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"1 to 5 mph\",\n \"windDirection\": \"E\",\n \"icon\": \"https://api.weather.gov/icons/land/day/sct?size=medium\",\n \"shortForecast\": \"Mostly Sunny\",\n \"detailedForecast\": \"Mostly sunny, with a high near 50.\"\n      },\n      {\n \"number\": 7,\n \"name\": \"Saturday Night\",\n \"startTime\": \"2021-11-20T18:00:00-05:00\",\n \"endTime\": \"2021-11-21T06:00:00-05:00\",\n \"isDaytime\": false,\n \"temperature\": 37,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"3 mph\",\n \"windDirection\": \"S\",\n \"icon\": \"https://api.weather.gov/icons/land/night/bkn?size=medium\",\n \"shortForecast\": \"Mostly Cloudy\",\n \"detailedForecast\": \"Mostly cloudy, with a low around 37.\"\n      },\n      {\n \"number\": 8,\n \"name\": \"Sunday\",\n \"startTime\": \"2021-11-21T06:00:00-05:00\",\n \"endTime\": \"2021-11-21T18:00:00-05:00\",\n \"isDaytime\": true,\n \"temperature\": 56,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"2 to 6 mph\",\n \"windDirection\": \"S\",\n \"icon\": \"https://api.weather.gov/icons/land/day/bkn?size=medium\",\n \"shortForecast\": \"Partly Sunny\",\n \"detailedForecast\": \"Partly sunny, with a high near 56.\"\n      },\n      {\n \"number\": 9,\n \"name\": \"Sunday Night\",\n \"startTime\": \"2021-11-21T18:00:00-05:00\",\n \"endTime\": \"2021-11-22T06:00:00-05:00\",\n \"isDaytime\": false,\n \"temperature\": 44,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"2 to 6 mph\",\n \"windDirection\": \"S\",\n \"icon\": \"https://api.weather.gov/icons/land/night/rain_showers,40?size=medium\",\n \"shortForecast\": \"Chance Rain Showers\",\n \"detailedForecast\": \"A chance of rain showers after 7pm. Mostly cloudy, with a low around 44. Chance of precipitation is 40%.\"\n      },\n      {\n \"number\": 10,\n \"name\": \"Monday\",\n \"startTime\": \"2021-11-22T06:00:00-05:00\",\n \"endTime\": \"2021-11-22T18:00:00-05:00\",\n \"isDaytime\": true,\n \"temperature\": 56,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"5 to 12 mph\",\n \"windDirection\": \"W\",\n \"icon\": \"https://api.weather.gov/icons/land/day/rain_showers,40?size=medium\",\n \"shortForecast\": \"Chance Rain Showers\",\n \"detailedForecast\": \"A chance of rain showers. Partly sunny, with a high near 56. Chance of precipitation is 40%.\"\n      },\n      {\n \"number\": 11,\n \"name\": \"Monday Night\",\n \"startTime\": \"2021-11-22T18:00:00-05:00\",\n \"endTime\": \"2021-11-23T06:00:00-05:00\",\n \"isDaytime\": false,\n \"temperature\": 32,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"12 mph\",\n \"windDirection\": \"NW\",\n \"icon\": \"https://api.weather.gov/icons/land/night/rain_showers,40/rain_showers?size=medium\",\n \"shortForecast\": \"Chance Rain Showers\",\n \"detailedForecast\": \"A chance of rain showers before 1am. Partly cloudy, with a low around 32. Chance of precipitation is 40%.\"\n      },\n      {\n \"number\": 12,\n \"name\": \"Tuesday\",\n \"startTime\": \"2021-11-23T06:00:00-05:00\",\n \"endTime\": \"2021-11-23T18:00:00-05:00\",\n \"isDaytime\": true,\n \"temperature\": 46,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"10 to 18 mph\",\n \"windDirection\": \"NW\",\n \"icon\": \"https://api.weather.gov/icons/land/day/sct?size=medium\",\n \"shortForecast\": \"Mostly Sunny\",\n \"detailedForecast\": \"Mostly sunny, with a high near 46.\"\n      },\n      {\n \"number\": 13,\n \"name\": \"Tuesday Night\",\n \"startTime\": \"2021-11-23T18:00:00-05:00\",\n \"endTime\": \"2021-11-24T06:00:00-05:00\",\n \"isDaytime\": false,\n \"temperature\": 33,\n \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"10 to 14 mph\",\n \"windDirection\": \"NW\",\n \"icon\": \"https://api.weather.gov/icons/land/night/few?size=medium\",\n \"shortForecast\": \"Mostly Clear\",\n \"detailedForecast\": \"Mostly clear, with a low around 33.\"\n      },\n      {\n \"number\": 14,\n \"name\": \"Wednesday\",\n \"startTime\": \"2021-11-24T06:00:00-05:00\",\n \"endTime\": \"2021-11-24T18:00:00-05:00\",\n\"isDaytime\": true,\n \"temperature\": 50,\n  \"temperatureUnit\": \"F\",\n \"temperatureTrend\": null,\n \"windSpeed\": \"8 to 12 mph\",\n\"windDirection\": \"W\",\n\"icon\": \"https://api.weather.gov/icons/land/day/few?size=medium\",\n \"shortForecast\": \"Sunny\",\n \"detailedForecast\": \"Sunny, with a high near 50.\"\n  }\n ]\n}\n}"

/** Will eventually become fleshed-out Weather Component of back-end */
object Weather {

    private val json = Json { ignoreUnknownKeys = true }

    /** Returns List of HalfDayWeather s */
    val forecasts = getNewestWeather()

    /** Prototype for Weather Component procedure of polling NWS and decoding JSON */
    private fun getNewestWeather(): List<HalfDayWeather>? {
        val jsonString = getWeatherJsonStringFromOnline(hardcoded_url)
        val element = json.parseToJsonElement(jsonString)

        val weatherElementList = element.jsonObject["properties"]?.jsonObject?.get("periods")?.jsonArray

        return weatherElementList?.map { json.decodeFromJsonElement(it) }
    }

    private fun getMockWeather() = weatherJSONString

    val hardcoded_url = URL("https://api.weather.gov/gridpoints/LWX/96,70/forecast")


    fun getWeatherJsonStringFromOnline(forecastUrl: URL): String {
        val string = (forecastUrl.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            setRequestProperty("User-Agent", "potato")
            requestMethod = "GET"
            doInput = true
            inputStream.use {
                it.bufferedReader().readText()
            }
        }
        return string!!
    }

}

/* Object that directly maps to above period object from GeoJSON */
/** Represents the forecast for half a day */
@Serializable
data class HalfDayWeather(
    @SerialName("startTime")
    private val zonedDateTimeString: String,
    val isDaytime: Boolean,
    val temperature: Int,
    private val windSpeed: String,
    val shortForecast: String,
    val detailedForecast: String,
) {
    @delegate: Ignore
    val windSpeedRange: IntRange by lazy {
        val matcher = numberPattern.matcher(windSpeed)
        matcher.find()
        val start = Integer.parseInt(matcher.group())
        val end = if (matcher.find()) Integer.parseInt(matcher.group()) else start
        IntRange(start, end)
    }
    @delegate: Ignore
    val zonedDateTime: ZonedDateTime by lazy { ZonedDateTime.parse(zonedDateTimeString) }

    // TODO: 10/25/2021  more sophisticated sky field and parsing
    @delegate: Ignore
    val sky: Sky by lazy {
        when {
            shortForecast.contains("Sunny") -> Sky.SUNNY
            shortForecast.contains("Cloudy") -> Sky.CLOUDY
            shortForecast.contains("Rain") -> Sky.RAINY
            else -> Sky.CLEAR
        }
    }

    companion object {
        private val numberPattern: Pattern = Pattern.compile("\\d+")
    }
}

data class DayWeather(val morningWeather: HalfDayWeather, val nightWeather: HalfDayWeather)

// TODO: 10/24/2021  refactor, perhaps compose Forecast from Likelihood+Condition + optional then Forecast
enum class Sky {
    SUNNY, CLOUDY, RAINY, CLEAR
}
