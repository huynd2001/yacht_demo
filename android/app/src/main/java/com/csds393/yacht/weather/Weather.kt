@file:UseSerializers(ZonedDateTimeAsStringSerializer::class)

package com.csds393.yacht.weather

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import java.net.*
import java.time.*
import java.util.regex.Pattern

/** Temp stand in for getting from online. */
private const val weatherJSONString = "{\"@context\":[\"https://geojson.org/geojson-ld/geojson-context.jsonld\",{\"@version\":\"1.1\",\"wx\":\"https://api.weather.gov/ontology#\",\"geo\":\"http://www.opengis.net/ont/geosparql#\",\"unit\":\"http://codes.wmo.int/common/unit/\",\"@vocab\":\"https://api.weather.gov/ontology#\"}],\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-77.036996200000004,38.900789000000003],[-77.040754800000002,38.878836500000006],[-77.012551900000005,38.875908600000002],[-77.008787600000005,38.897860800000004],[-77.036996200000004,38.900789000000003]]]},\"properties\":{\"updated\":\"2021-10-24T14:31:32+00:00\",\"units\":\"us\",\"forecastGenerator\":\"BaselineForecastGenerator\",\"generatedAt\":\"2021-10-24T14:42:50+00:00\",\"updateTime\":\"2021-10-24T14:31:32+00:00\",\"validTimes\":\"2021-10-24T08:00:00+00:00/P7DT17H\",\"elevation\":{\"unitCode\":\"wmoUnit:m\",\"value\":6.0960000000000001},\"periods\":[{\"number\":1,\"name\":\"Today\",\"startTime\":\"2021-10-24T10:00:00-04:00\",\"endTime\":\"2021-10-24T18:00:00-04:00\",\"isDaytime\":true,\"temperature\":71,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"3 to 7 mph\",\"windDirection\":\"S\",\"icon\":\"https://api.weather.gov/icons/land/day/rain_showers,20/bkn?size=medium\",\"shortForecast\":\"Slight Chance Rain Showers then Partly Sunny\",\"detailedForecast\":\"A slight chance of rain showers before 11am. Partly sunny, with a high near 71. South wind 3 to 7 mph. Chance of precipitation is 20%.\"},{\"number\":2,\"name\":\"Tonight\",\"startTime\":\"2021-10-24T18:00:00-04:00\",\"endTime\":\"2021-10-25T06:00:00-04:00\",\"isDaytime\":false,\"temperature\":58,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"6 mph\",\"windDirection\":\"S\",\"icon\":\"https://api.weather.gov/icons/land/night/sct?size=medium\",\"shortForecast\":\"Partly Cloudy\",\"detailedForecast\":\"Partly cloudy, with a low around 58. South wind around 6 mph.\"},{\"number\":3,\"name\":\"Monday\",\"startTime\":\"2021-10-25T06:00:00-04:00\",\"endTime\":\"2021-10-25T18:00:00-04:00\",\"isDaytime\":true,\"temperature\":77,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"6 to 10 mph\",\"windDirection\":\"S\",\"icon\":\"https://api.weather.gov/icons/land/day/bkn/tsra_sct,60?size=medium\",\"shortForecast\":\"Partly Sunny then Showers And Thunderstorms Likely\",\"detailedForecast\":\"A chance of rain showers between 2pm and 5pm, then showers and thunderstorms likely. Partly sunny, with a high near 77. South wind 6 to 10 mph, with gusts as high as 21 mph. Chance of precipitation is 60%. New rainfall amounts between a quarter and half of an inch possible.\"},{\"number\":4,\"name\":\"Monday Night\",\"startTime\":\"2021-10-25T18:00:00-04:00\",\"endTime\":\"2021-10-26T06:00:00-04:00\",\"isDaytime\":false,\"temperature\":57,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"6 mph\",\"windDirection\":\"S\",\"icon\":\"https://api.weather.gov/icons/land/night/tsra,70?size=medium\",\"shortForecast\":\"Showers And Thunderstorms Likely\",\"detailedForecast\":\"Showers and thunderstorms likely. Mostly cloudy, with a low around 57. South wind around 6 mph. Chance of precipitation is 70%. New rainfall amounts between a half and three quarters of an inch possible.\"},{\"number\":5,\"name\":\"Tuesday\",\"startTime\":\"2021-10-26T06:00:00-04:00\",\"endTime\":\"2021-10-26T18:00:00-04:00\",\"isDaytime\":true,\"temperature\":65,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"6 to 14 mph\",\"windDirection\":\"NW\",\"icon\":\"https://api.weather.gov/icons/land/day/tsra_hi,70/tsra_hi,30?size=medium\",\"shortForecast\":\"Showers And Thunderstorms Likely\",\"detailedForecast\":\"Showers and thunderstorms likely. Partly sunny, with a high near 65. Northwest wind 6 to 14 mph, with gusts as high as 29 mph. Chance of precipitation is 70%.\"},{\"number\":6,\"name\":\"Tuesday Night\",\"startTime\":\"2021-10-26T18:00:00-04:00\",\"endTime\":\"2021-10-27T06:00:00-04:00\",\"isDaytime\":false,\"temperature\":51,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"10 to 14 mph\",\"windDirection\":\"NW\",\"icon\":\"https://api.weather.gov/icons/land/night/rain_showers,30/rain_showers,20?size=medium\",\"shortForecast\":\"Chance Rain Showers\",\"detailedForecast\":\"A chance of rain showers before 2am. Mostly cloudy, with a low around 51. Chance of precipitation is 30%.\"},{\"number\":7,\"name\":\"Wednesday\",\"startTime\":\"2021-10-27T06:00:00-04:00\",\"endTime\":\"2021-10-27T18:00:00-04:00\",\"isDaytime\":true,\"temperature\":65,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"12 mph\",\"windDirection\":\"NW\",\"icon\":\"https://api.weather.gov/icons/land/day/bkn?size=medium\",\"shortForecast\":\"Partly Sunny\",\"detailedForecast\":\"Partly sunny, with a high near 65.\"},{\"number\":8,\"name\":\"Wednesday Night\",\"startTime\":\"2021-10-27T18:00:00-04:00\",\"endTime\":\"2021-10-28T06:00:00-04:00\",\"isDaytime\":false,\"temperature\":51,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"2 to 8 mph\",\"windDirection\":\"N\",\"icon\":\"https://api.weather.gov/icons/land/night/sct?size=medium\",\"shortForecast\":\"Partly Cloudy\",\"detailedForecast\":\"Partly cloudy, with a low around 51.\"},{\"number\":9,\"name\":\"Thursday\",\"startTime\":\"2021-10-28T06:00:00-04:00\",\"endTime\":\"2021-10-28T18:00:00-04:00\",\"isDaytime\":true,\"temperature\":66,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"2 to 7 mph\",\"windDirection\":\"NE\",\"icon\":\"https://api.weather.gov/icons/land/day/bkn/rain_showers?size=medium\",\"shortForecast\":\"Partly Sunny then Slight Chance Rain Showers\",\"detailedForecast\":\"A slight chance of rain showers after 2pm. Partly sunny, with a high near 66.\"},{\"number\":10,\"name\":\"Thursday Night\",\"startTime\":\"2021-10-28T18:00:00-04:00\",\"endTime\":\"2021-10-29T06:00:00-04:00\",\"isDaytime\":false,\"temperature\":54,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"7 mph\",\"windDirection\":\"E\",\"icon\":\"https://api.weather.gov/icons/land/night/rain_showers,40/rain_showers,50?size=medium\",\"shortForecast\":\"Chance Rain Showers\",\"detailedForecast\":\"A chance of rain showers. Mostly cloudy, with a low around 54. Chance of precipitation is 50%.\"},{\"number\":11,\"name\":\"Friday\",\"startTime\":\"2021-10-29T06:00:00-04:00\",\"endTime\":\"2021-10-29T18:00:00-04:00\",\"isDaytime\":true,\"temperature\":66,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"6 to 9 mph\",\"windDirection\":\"SE\",\"icon\":\"https://api.weather.gov/icons/land/day/rain_showers,50/rain_showers,60?size=medium\",\"shortForecast\":\"Rain Showers Likely\",\"detailedForecast\":\"Rain showers likely. Mostly cloudy, with a high near 66. Chance of precipitation is 60%.\"},{\"number\":12,\"name\":\"Friday Night\",\"startTime\":\"2021-10-29T18:00:00-04:00\",\"endTime\":\"2021-10-30T06:00:00-04:00\",\"isDaytime\":false,\"temperature\":52,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"3 to 7 mph\",\"windDirection\":\"SE\",\"icon\":\"https://api.weather.gov/icons/land/night/rain_showers,60/rain_showers,40?size=medium\",\"shortForecast\":\"Rain Showers Likely\",\"detailedForecast\":\"Rain showers likely. Mostly cloudy, with a low around 52. Chance of precipitation is 60%.\"},{\"number\":13,\"name\":\"Saturday\",\"startTime\":\"2021-10-30T06:00:00-04:00\",\"endTime\":\"2021-10-30T18:00:00-04:00\",\"isDaytime\":true,\"temperature\":63,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"3 to 8 mph\",\"windDirection\":\"SW\",\"icon\":\"https://api.weather.gov/icons/land/day/rain_showers,30/rain_showers,40?size=medium\",\"shortForecast\":\"Chance Rain Showers\",\"detailedForecast\":\"A chance of rain showers. Partly sunny, with a high near 63. Chance of precipitation is 40%.\"},{\"number\":14,\"name\":\"Saturday Night\",\"startTime\":\"2021-10-30T18:00:00-04:00\",\"endTime\":\"2021-10-31T06:00:00-04:00\",\"isDaytime\":false,\"temperature\":49,\"temperatureUnit\":\"F\",\"temperatureTrend\":null,\"windSpeed\":\"7 mph\",\"windDirection\":\"NW\",\"icon\":\"https://api.weather.gov/icons/land/night/rain_showers,40/rain_showers?size=medium\",\"shortForecast\":\"Chance Rain Showers\",\"detailedForecast\":\"A chance of rain showers. Mostly cloudy, with a low around 49. Chance of precipitation is 40%.\"}]}}\n"

/** Will eventually become fleshed-out Weather Component of back-end */
object Weather {

    private val json = Json { ignoreUnknownKeys = true }

    /** Returns List of HalfDayWeather s */
    val forecasts = getNewestWeather()

    /** Prototype for Weather Component procedure of polling NWS and decoding JSON */
    @JvmStatic
    private fun getNewestWeather(): List<HalfDayWeather>? {
        val jsonString = getMockWeather()
        val element = json.parseToJsonElement(jsonString)

        val weatherElementList = element.jsonObject["properties"]?.jsonObject?.get("periods")?.jsonArray

        return weatherElementList?.map { json.decodeFromJsonElement(it) }
    }

    private fun getMockWeather() = weatherJSONString

    // TODO: 10/22/2021  parameterize URL and/or location
    // TODO: 10/24/2021  doesn't work in android emulator. port to some library?
    private fun getWeatherJsonStringFromOnline(): String {
        val url = URL("https://api.weather.gov/gridpoints/LWX/96,70/forecast")
        val string = (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            // Starts the query
            connect()
            inputStream
                .bufferedReader()
                .readText()
        }
        return string!!
    }

}
//"periods":[
// example of JSON element for single weather (half day)
// {"number":1,"name":"Today","startTime":"2021-10-24T10:00:00-04:00",
// "endTime":"2021-10-24T18:00:00-04:00","isDaytime":true,
// "temperature":71,"temperatureUnit":"F","temperatureTrend":null,
// "windSpeed":"3 to 7 mph","windDirection":"S",
// "icon":"https://api.weather.gov/icons/land/day/rain_showers,20/bkn?size=medium",
// "shortForecast":"Slight Chance Rain Showers then Partly Sunny",
// "detailedForecast":"A slight chance of rain showers before 11am. Partly sunny, with a high near 71. South wind 3 to 7 mph. Chance of precipitation is 20%."},

/* Object that directly maps to above period object from GeoJSON */
/** Represents the forecast for half a day */
@Serializable
data class HalfDayWeather(
    @SerialName("startTime")
    val zonedDate: ZonedDateTime,
    val isDaytime: Boolean,
    val temperature: Int,
    private val windSpeed: String,
    val shortForecast: String,
    val detailedForecast: String,
) {
    val windSpeedRange: IntRange
        get() {
            val numberPattern = Pattern.compile("\\d+")
            val matcher = numberPattern.matcher(windSpeed)
            matcher.find()
            val start = Integer.parseInt(matcher.group())
            val end = if (matcher.find()) Integer.parseInt(matcher.group()) else start
            return IntRange(start, end)
        }
    val localDate: LocalDate
        get() = zonedDate.toLocalDate()

    val sky: Sky
        get() {
            return when {
                shortForecast.contains("Sunny") -> Sky.SUNNY
                shortForecast.contains("Cloudy") -> Sky.CLOUDY
                shortForecast.contains("Rain") -> Sky.RAINY
                else -> Sky.CLEAR
            }
        }
    fun toWeatherData(): WeatherData {
        return WeatherData(localDate.dayOfWeek, temperature, sky, windSpeedRange.first)
    }
}


// TODO: 10/24/2021  refactor, perhaps compose Forecast from Likelihood+Condition + optional then Forecast
enum class Sky {
    SUNNY, CLOUDY, RAINY, CLEAR

}


object ZonedDateTimeAsStringSerializer: KSerializer<ZonedDateTime> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ZonedDateTimeAsStringSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ZonedDateTime =
        ZonedDateTime.parse(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: ZonedDateTime) =
        encoder.encodeString(value.toString())
}
