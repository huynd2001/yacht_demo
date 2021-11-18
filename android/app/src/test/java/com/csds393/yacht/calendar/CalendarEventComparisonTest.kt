package com.csds393.yacht.calendar

import org.junit.Assert.*

import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

/**
 * Tests the ordering of CalendarEvents and Details.
 * Should match method descriptions
 */
class CalendarEventComparisonTest {

    /** Tests are designed around the assumption that eventA has no null fields */
    private val eventA = CalendarEvent(
        LocalDate.of(2041, 6, 22),
        LocalTime.of(2, 3),
        LocalDate.of(2096, 4, 11),
        LocalTime.of(2, 3),
        CalendarEvent.Details("label")
    )
    lateinit var eventB: CalendarEvent


    /*
     * CalendarEvent
     * Earlier [startDate],
     * Absent [startTime] > earlier [startTime],
     * Earlier [endDate],
     * Earlier [endTime] > absent [endTime],
     * Compare [details]
     */


    @Test
    fun `compareTo self`() {
        assertEquals(0, eventA.compareTo(eventA))
    }

    @Test
    fun `compareTo copy`() {
        eventB = eventA.copy()
        assertEquals(0, eventA.compareTo(eventB))
        assertEquals(0, eventB.compareTo(eventA))
    }

    @Test
    fun `compareTo earlier startDate`() {
        eventB = eventA.copy(startDate = eventA.startDate.plusDays(1))
        val compareResult = eventA.compareTo(eventB)
        assertTrue(compareResult < 0)
    }

    @Test
    fun `compareTo absent startTime lesser than present`() {
        eventB = eventA.copy(startTime = null)
        val compareResult = eventA.compareTo(eventB)
        assertTrue(0 < compareResult)
    }
    @Test
    fun `compareTo earlier startTime`() {
        eventB = eventA.copy(startTime = eventA.startTime!!.plusSeconds(1))
        val compareResult = eventA.compareTo(eventB)
        assertTrue(compareResult < 0)
    }

    @Test
    fun `compareTo earlier endDate`() {
        eventB = eventA.copy(endDate = eventA.endDate.plusDays(1))
        val compareResult = eventA.compareTo(eventB)
        assertTrue(compareResult < 0)
    }

    @Test
    fun `compareTo present endTime lesser than absent`() {
        eventB = eventA.copy(endTime = null)
        val compareResult = eventA.compareTo(eventB)
        assertTrue(compareResult < 0)
    }

    @Test
    fun `compareTo earlier endTime`() {
        eventB = eventA.copy(endTime = eventA.endTime!!.plusSeconds(1))
        val compareResult = eventA.compareTo(eventB)
        assertTrue(compareResult < 0)
    }

    @Test
    fun `compareTo lesser Details`() {
        eventB = eventA.copy(details = eventA.details.copy(label = eventA.details.label + "z"))
        val compareResult = eventA.compareTo(eventB)
        assertTrue(compareResult < 0)
    }

    /*
     * Details
     * Label (string)
     * Description (string)
     */

    @Test
    fun `compareTo lesser label`() {
        val lesserDetails = CalendarEvent.Details("a")
        val greaterDetails = CalendarEvent.Details("b")
        val compareResult = lesserDetails.compareTo(greaterDetails)
        assertTrue(compareResult < 0)
    }

    @Test
    fun `compareTo lesser description`() {
        val lesserDetails = CalendarEvent.Details("label", "a")
        val greaterDetails = CalendarEvent.Details("label", "b")
        val compareResult = lesserDetails.compareTo(greaterDetails)
        assertTrue(compareResult < 0)
    }

}
