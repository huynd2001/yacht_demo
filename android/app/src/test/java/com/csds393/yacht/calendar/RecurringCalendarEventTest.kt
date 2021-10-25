package com.csds393.yacht.calendar



import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.*

class RecurringCalendarEventTest {

    private val eventBase = CalendarEvent(LocalDate.MIN, details = CalendarEvent.Details("label"))
    /* Range used by RCE */
    private val earliestValidDate = LocalDate.of(1942, 12, 1)
    private val latestValidDate = LocalDate.of(2142, 3, 12)
    private val validWindow = earliestValidDate..latestValidDate

    private val preWindow = LocalDate.MIN..validWindow.start.minusDays(1)
    private val postWindow = validWindow.endInclusive.plusDays(1)..LocalDate.MAX
    private val overlappingWindow = earliestValidDate.minusMonths(8)..earliestValidDate.plusMonths(8)

    private val windowedRCEBase = RecurringCalendarEvent(eventBase, validWindow, DatePattern.forDaily())
    private val rceDaily = windowedRCEBase.copy(datePattern = DatePattern.forDaily())
    private val rceEveryNDays = windowedRCEBase.copy(datePattern = DatePattern.forEveryNDaysFrom(LocalDate.of(2016, Month.FEBRUARY, 23), 2))
    private val rceWeekly = windowedRCEBase.copy(datePattern = DatePattern.forWeekly(DayOfWeek.TUESDAY))
    private val rceMonthly = windowedRCEBase.copy(datePattern = DatePattern.forMonthly(23))
    private val rceAnnual = windowedRCEBase.copy(datePattern = DatePattern.forAnnual(MonthDay.of(Month.JUNE, 2)))
    private val allWindowedRCEs = listOf(rceDaily, rceEveryNDays, rceWeekly, rceMonthly, rceAnnual)



    /** No events should be generated */
    @Test
    fun `generateEvents preWindow should be empty`() {
        for (rce in allWindowedRCEs) assertTrue(rce.generateEventsBetween(preWindow).isEmpty())
    }

    /** No events should be generated */
    @Test
    fun `generateEvents postWindow should be empty`() {
        for (rce in allWindowedRCEs) assertTrue(rce.generateEventsBetween(postWindow).isEmpty())
    }


    @Test
    fun `occursWithinWindow before window`() {
        for (rce in allWindowedRCEs) assertFalse(rce.occursWithinWindow(preWindow))
    }

    @Test
    fun `occursWithinWindow overlapping window`() {
        for (rce in allWindowedRCEs) assertTrue(rce.occursWithinWindow(overlappingWindow))
    }

    @Test
    fun `occursWithinWindow after window`() {
        for (rce in allWindowedRCEs) assertFalse(rce.occursWithinWindow(postWindow))
    }
}
