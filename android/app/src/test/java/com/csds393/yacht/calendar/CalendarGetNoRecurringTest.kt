package com.csds393.yacht.calendar

import com.csds393.yacht.calendar.Calendar.QueryResult.Companion.EMPTY_RESULT
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import java.time.LocalDate


/**
 * Tests retrieval operations on an empty Calendar and a populated Calendar.
 * In either case, no recurring tests. See other test class for those tests
 */
class CalendarGetNoRecurringTest {

    lateinit var cal: Calendar
    lateinit var sortedList: List<CalendarEvent>
    lateinit var recurringList: List<RecurringCalendarEvent>
    private val labels = listOf("A", "B", "C", "Absent Label")


    @Before
    fun setUp() {
        val events = listOf(
            CalendarEvent(LocalDate.of(2004, 4, 25), details = CalendarEvent.Details("C", "abc")),
            CalendarEvent(LocalDate.of(1912, 3, 1), details = CalendarEvent.Details("A")),
            CalendarEvent(LocalDate.of(2000, 12, 30), details = CalendarEvent.Details("A")),
            CalendarEvent(LocalDate.of(1943, 2, 13), details = CalendarEvent.Details("B", "123")),
            CalendarEvent(LocalDate.of(2204, 9, 3), details = CalendarEvent.Details("C", "event"))
        )

        cal = Calendar(events)
        sortedList = events.sorted()
        recurringList = listOf()
    }


    @Test
    fun get_fromEmpty() {
        cal = Calendar(emptyList())
        assertEquals(EMPTY_RESULT, cal.getAll())
        assertEquals(EMPTY_RESULT, cal.getAfter(LocalDate.MIN))
        assertEquals(EMPTY_RESULT, cal.getBetween(LocalDate.MIN, LocalDate.MAX))
        assertEquals(EMPTY_RESULT, cal.getBefore(LocalDate.MAX))
    }

    @Test
    fun get_all() {
        val allResult = Calendar.QueryResult(sortedList, recurringList)
        assertEquals(allResult, cal.getAll())
        assertEquals(allResult, cal.getAfter(LocalDate.MIN))
        assertEquals(allResult, cal.getBetween(LocalDate.MIN, LocalDate.MAX))
        assertEquals(allResult, cal.getBefore(LocalDate.MAX))
    }

    @Test
    fun getAll_label() {
        for (label in labels) {
            val queryResult = Calendar.QueryResult(
                sortedList.filter { it.details.label == label },
                recurringList.filter { it.details.label == label }
            )
            assertEquals(
                queryResult,
                cal.getAll { it?.label == label }
            )
        }
    }

    @Test
    fun getAfter_last() {
        assertEquals(EMPTY_RESULT, cal.getAfter(sortedList.last().startDate.plusDays(1)))
    }

    @Test
    fun getAfter_withLabel() {
        for (i in sortedList.indices) for (label in labels) {
            val queryResult = Calendar.QueryResult(
                sortedList.subList(i, sortedList.size).filter { it.details.label == label },
                recurringList.filter { it.details.label == label })
            assertEquals(queryResult, cal.getAfter(sortedList[i].startDate) { it.label == label })
        }
    }

    @Test
    fun getBetween_crossed() {
        val day = LocalDate.of(1, 2, 3)
        assertEquals(EMPTY_RESULT, cal.getBetween(day.plusDays(1), day))
    }

    @Test
    fun getBetween() {
        for (i in sortedList.indices) for (j in i..sortedList.lastIndex) {
            val start = sortedList[i].startDate
            val end = sortedList[j].startDate
            val queryResult = Calendar.QueryResult(
                sortedList.subList(i, j+1),
                recurringList)
            assertEquals(queryResult, cal.getBetween(start, end))
        }
    }

    @Test
    fun getBefore_first() {
        assertEquals(EMPTY_RESULT, cal.getBefore(sortedList.first().startDate.minusDays(1)))
    }

    @Test
    fun getBefore() {
        for (i in sortedList.indices) {
            val queryResult = Calendar.QueryResult(sortedList.take(i+1), recurringList)
            assertEquals(queryResult, cal.getBefore(sortedList[i].startDate))
        }
    }

    @Test
    fun getBefore_dateWithLabel() {
        for (i in sortedList.indices) for (label in labels) {
            val queryResult = Calendar.QueryResult(
                sortedList.take(i+1).filter { it.details.label == label },
                recurringList.filter { it.details.label == label })
            assertEquals(
                queryResult,
                cal.getBefore(sortedList[i].startDate) { it.label == label }
            )
        }
    }
}
