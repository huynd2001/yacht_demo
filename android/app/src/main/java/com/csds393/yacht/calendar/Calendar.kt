package com.csds393.yacht.calendar

import kotlin.jvm.JvmOverloads
import java.time.LocalDate
import java.util.function.Predicate

/**
 * The Calendar stores and manages the entries of the calendar.
 * It behaves like a database for instances of CalendarEvent.
 * @property calendarEvents  Sorted list of CalendarEvents
 * @property recurringCalendarEvents  Unsorted list of RecurringCalendarEvents
 */
class Calendar @JvmOverloads constructor(
    calendarEvents: List<CalendarEvent> = listOf(),
    recurringCalendarEvents: List<RecurringCalendarEvent> = listOf()
) {
    private val calendarEvents: MutableList<CalendarEvent> = calendarEvents.sorted().toMutableList()
    private val recurringCalendarEvents = recurringCalendarEvents.toMutableList()

    /** Returns a sorted List of all the CalendarEvents currently in this Calendar */
    fun getAll() = QueryResult(calendarEvents.toList(), recurringCalendarEvents.toList())

    /** Returns a sorted List of the CalendarEvents that fit the [detailsPredicate] */
    fun getAll(detailsPredicate: Predicate<CalendarEvent.Details?>): QueryResult {
        val (norm, recur) = getAll()
        return QueryResult(
            norm.filter { (_, _, _, _, details) -> detailsPredicate.test(details) },
            recur.filter { recCalEv -> detailsPredicate.test(recCalEv.details) }
        )
    }

    /** Returns a sorted List of the CalendarEvents after [start], inclusive, that satisfy [detailsPredicate] */
    @JvmOverloads fun getAfter(
        start: LocalDate,
        detailsPredicate: Predicate<CalendarEvent.Details> = Predicate { true }
    ) = get(start, LocalDate.MAX, detailsPredicate)

    /**
     * Returns a sorted List of the CalendarEvents after [start], inclusive,
     * and before [end], inclusive, that satisfy [detailsPredicate]
     */
    @JvmOverloads fun getBetween(
        start: LocalDate, end: LocalDate,
        detailsPredicate: Predicate<CalendarEvent.Details> = Predicate { true }
    ) = get(start, end, detailsPredicate)

    /** Returns a sorted List of the CalendarEvents before [end] Date, inclusive, that satisfy [detailsPredicate] */
    @JvmOverloads fun getBefore(
        end: LocalDate,
        detailsPredicate: Predicate<CalendarEvent.Details> = Predicate { true }
    ) = get(LocalDate.MIN, end, detailsPredicate)

    // simplest implementation for now.
    // TODO: 10/16/2021  for efficiency, binary search for first and last indices
    private operator fun get(
        start: LocalDate,
        end: LocalDate,
        detailsPredicate: Predicate<CalendarEvent.Details>
    ): QueryResult {
        return if (start.isAfter(end)) QueryResult.EMPTY_RESULT
        else {
            val window = start..end
            QueryResult(
                calendarEvents.filter { (startDate, _, _, _, details) ->
                    startDate in window && detailsPredicate.test(details) },
                recurringCalendarEvents.filter { recCalEv ->
                    recCalEv.occursWithinWindow(window) && detailsPredicate.test(recCalEv.details) }
            )
        }
    }

    /** Adds [event], if it isn't already present, and returns whether [event] was added */
    fun add(event: CalendarEvent): Boolean {
        val idx = calendarEvents.binarySearch(event)
        if (idx >= 0) return false // already present
        calendarEvents.add(-(idx + 1), event) // absent, so have to negate idx according to binarySearch docs
        return true
    }

    /** Adds [recurringEvent] if not present and returns whether added */
    fun add(recurringEvent: RecurringCalendarEvent): Boolean {
        return if (recurringEvent in recurringCalendarEvents) false
        else recurringCalendarEvents.add(recurringEvent)
    }

    /** Removes the first occurrence of [event], returns whether [event] was remove */
    fun remove(event: CalendarEvent): Boolean {
        val idx = calendarEvents.binarySearch(event)
        if (idx < 0) return false
        calendarEvents.removeAt(idx)
        return true
    }

    /** Returns whether [recurringEvent] was present and removes it */
    fun remove(recurringEvent: RecurringCalendarEvent) = recurringCalendarEvents.remove(recurringEvent)

    /** Tuple of List<CalendarEvent> and List<recurringCalendarEvents> */
    data class QueryResult internal constructor(
        val calendarEvents: List<CalendarEvent>,
        val recurringCalendarEvents: List<RecurringCalendarEvent>
        ) {
        companion object {
            @JvmStatic val EMPTY_RESULT = QueryResult(emptyList(), emptyList())
        }
    }
}
