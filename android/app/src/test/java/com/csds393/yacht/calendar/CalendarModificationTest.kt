package com.csds393.yacht.calendar

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/** Tests addition and removal of events */
class CalendarModificationTest {

    private val earlyEvent = CalendarEvent(LocalDate.of(1, 2, 3), details = CalendarEvent.Details("C", "words"))
    private val middleEvent = CalendarEvent(LocalDate.of(2014, 3, 12), details = CalendarEvent.Details("E", "words"))
    private val futureEvent = CalendarEvent(LocalDate.of(5122, 2, 24), details = CalendarEvent.Details("D", "words"))

    private lateinit var cal: Calendar
    private lateinit var sortedList: List<CalendarEvent>
    private lateinit var recurringList: List<RecurringCalendarEvent>

    private fun populate() = sortedList.forEach(cal::add)

    @Before
    fun setUp() {
        cal = Calendar()

        val events = listOf(
            CalendarEvent(LocalDate.of(2004, 4, 25), details = CalendarEvent.Details("C", "description")),
            CalendarEvent(LocalDate.of(1912, 3, 1), details = CalendarEvent.Details("A", "event")),
            CalendarEvent(LocalDate.of(2000, 12, 30), details = CalendarEvent.Details("A", "words")),
            CalendarEvent(LocalDate.of(1943, 2, 13), details = CalendarEvent.Details("B")),
            CalendarEvent(LocalDate.of(2204, 9, 3), details = CalendarEvent.Details("C", "test"))
        )

        sortedList = events.sorted()
        recurringList = listOf()
    }

    @Test
    fun add_1_empty() {
        assertTrue(cal.add(earlyEvent))
        assertEquals(listOf(earlyEvent), cal.getAll().calendarEvents)
        assertTrue(cal.getAll().recurringCalendarEvents.isEmpty())
    }

    @Test
    fun add_redundant_empty() {
        cal.add(earlyEvent)
        val firstQueryResult = cal.getAll()
        assertFalse(cal.add(earlyEvent))
        assertEquals(firstQueryResult, cal.getAll())
    }

    @Test
    fun add_redundant() {
        populate()
        val correctList = listOf(earlyEvent).plus(sortedList)

        assertTrue(cal.add(earlyEvent))
        assertEquals(correctList, cal.getAll().calendarEvents)

        assertFalse(cal.add(earlyEvent))
        assertEquals(correctList, cal.getAll().calendarEvents)
    }

    @Test
    fun add_multipleUnique_empty() {
        assertTrue(cal.add(earlyEvent))
        assertTrue(cal.add(middleEvent))
        assertTrue(cal.add(futureEvent))

        val correctList = listOf(earlyEvent, middleEvent, futureEvent).sorted()

        assertEquals(correctList, cal.getAll().calendarEvents)
    }

    @Test
    fun remove_empty() {
        val prevQueryResult = cal.getAll()

        assertFalse(cal.remove(earlyEvent))
        assertEquals(prevQueryResult, cal.getAll())
    }

    @Test
    fun remove_absent_nonempty() {
        populate()
        val prevQueryResult = cal.getAll()

        assertFalse(cal.remove(earlyEvent))
        assertEquals(prevQueryResult, cal.getAll())
    }

    @Test
    fun addThenRemove_empty() {
        val prevQueryResult = cal.getAll()

        assertTrue(cal.add(earlyEvent))
        assertTrue(cal.remove(earlyEvent))
        assertEquals(prevQueryResult, cal.getAll())
    }

    @Test
    fun addThenRemove_nonempty() {
        populate()
        val prevQueryResult = cal.getAll()

        assertTrue(cal.add(earlyEvent))
        assertTrue(cal.remove(earlyEvent))

        assertEquals(prevQueryResult, cal.getAll())
    }

    @Test
    fun addThenRemove_multiple_nonempty() {
        populate()
        val prevQueryResult = cal.getAll()

        assertTrue(cal.add(earlyEvent))
        assertTrue(cal.add(futureEvent))
        assertTrue(cal.add(middleEvent))

        assertTrue(cal.remove(futureEvent))
        assertTrue(cal.remove(middleEvent))
        assertTrue(cal.remove(earlyEvent))

        assertEquals(prevQueryResult, cal.getAll())
    }


}
