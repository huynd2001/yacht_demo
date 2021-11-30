package com.csds393.yacht.calendar

import androidx.room.*
import java.time.*

/**
 * A CalendarEvent instance represents a single event on the Calendar.
 * An instance encapsulates the data associated with that event.
 * Deeply immutable.
 */
@Entity(tableName = "normal_events")
data class CalendarEvent internal constructor(
    val startDate: LocalDate,
    val startTime: LocalTime? = null,
    val endDate: LocalDate = startDate,
    val endTime: LocalTime? = null,
    @Embedded
    val details: Details,
    @PrimaryKey(autoGenerate = true) val id: Long? = null
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

    /** Returns a map of fieldName:fieldAsString */
    fun toMap() = buildMap {
        put("startDate", startDate.toString())
        startTime?.let { put("startTime", it.toString()) }
        put("endDate", endDate.toString())
        endTime?.let { put("endTime", it.toString()) }
        id?.let { put("id", id.toString()) }
        put("label", details.label)
        put("description", details.description)
    }

    companion object {
        /** Converts a String:String map into a CalendarEvent */
        @JvmStatic
        fun fromMap(map: Map<String, String>) = CalendarEvent(
                LocalDate.parse(map.getValue("startDate")),
                map["startTime"]?.let { LocalTime.parse(it) },
                LocalDate.parse(map.getValue("endDate")),
                map["endTime"]?.let { LocalTime.parse(it) },
                Details(
                        map.getValue("label"),
                        map.getValue("description")
                ),
                map["id"]?.toLongOrNull()
        )
    }

    // TODO: 10/26/2021  probably migrate Icon to resource reference (URI? Int?)
    /**
     * Encapsulates the non-temporal attributes of a CalendarEvent.
     * These include the label and description, among several others.
     */
    data class Details(
        val label: String,
        val description: String = "",
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
        }
    }
}
