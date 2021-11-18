@file:UseSerializers(DateAsStringSerializer::class, TimeAsStringSerializer::class)

package com.csds393.yacht.calendar

import android.graphics.drawable.Icon
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.time.*

/**
 * A CalendarEvent instance represents a single event on the Calendar.
 * An instance encapsulates the data associated with that event.
 * Deeply immutable.
 */
@Serializable
data class CalendarEvent (
        val startDate: LocalDate,
        val startTime: LocalTime? = null,
        val endDate: LocalDate = startDate,
        val endTime: LocalTime? = null,
        val details: Details
        ) : Comparable<CalendarEvent> {

    /**
     * Returns a negative integer, zero, or a positive integer as this CalendarEvent
     * is less than, equal to, or greater than the [other] CalendarEvent.
     * Ranks by:
     * Earlier [startDate],
     * Absent [startTime] > earlier [startTime],
     * Earlier [endDate],
     * Earlier [endTime] > absent [endTime],
     * Compare [details]
     */
    override fun compareTo(other: CalendarEvent): Int {
        if (this == other) return 0

        // earlier start date
        var comp = startDate.compareTo(other.startDate)
        if (comp != 0) return comp

        // Absent startTime, earlier startTime
        comp = (startTime?: LocalTime.MIN).compareTo(other.startTime?: LocalTime.MIN)
        if (comp != 0) return comp

        // earlier endDate
        comp = (endDate).compareTo(other.endDate)
        if (comp != 0) return comp

        // Earlier endTime, absent endTime
        comp = (endTime?: LocalTime.MAX).compareTo(other.endTime?: LocalTime.MAX)
        if (comp != 0) return comp

        return details.compareTo(other.details)
    }

    // TODO: 10/26/2021  probably migrate Icon to resource reference (URI?)
    /**
     * Encapsulates the non-temporal attributes of a CalendarEvent.
     * These include the label and description, among several others.
     * @property  icon  thumbnail
     * @property  image  full-size picture or doodle
     */
    @Serializable
    data class Details @JvmOverloads constructor(
            val label: String,
            val description: String = "",
//            val icon: Icon? = null,
//            val image: Icon? = null,
            ) : Comparable<Details> {

        /**
         * a negative integer, zero, or a positive integer as this Details
         * is less than, equal to, or greater than the [other] Details.
         * Ranks by:
         * [label],
         * [description]
         */
        override fun compareTo(other: Details): Int {
            if (this == other) return 0

            val comp = label.compareTo(other.label)
            if (comp != 0) return comp

            return description.compareTo(other.description)
            // TODO: 10/17/2021  possibly compare media fields
        }
    }
}

object DateAsStringSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DateAsStringSerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString())
}

object TimeAsStringSerializer : KSerializer<LocalTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TimeAsStringSerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LocalTime) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): LocalTime = LocalTime.parse(decoder.decodeString())
}
