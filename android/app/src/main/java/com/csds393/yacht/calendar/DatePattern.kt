package com.csds393.yacht.calendar

import androidx.annotation.IntRange
import java.lang.IllegalArgumentException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.MonthDay
import java.time.Period
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters.*

/**
 * A pattern for Dates.
 * Ex: Every Monday, every 3 days, 2nd day of every month, every June 6th
 */
// TODO: 10/20/2021  perhaps function returns list of valid Dates given a window?
interface DatePattern {

    /** Returns the soonest applicable date strictly after [date] */
    fun nextOccurrenceAfter(date: LocalDate) = nextOccurrenceFrom(date.plusDays(1))

    /** Returns the soonest applicable date on or after [date] */
    fun nextOccurrenceFrom(date: LocalDate): LocalDate

    companion object { // factory methods
        /** Returns a DatePattern for 'once a day' */
        @JvmStatic
        fun forDaily():DatePattern = DailyPattern

        /** Returns a DatePattern for 'once every [nDays], starting from [baseDate] and on' */
        @JvmStatic
        fun forEveryNDaysFrom(baseDate: LocalDate, @IntRange(from = 1) nDays: Int): DatePattern {
            if (nDays < 1) throw IllegalArgumentException("$nDays is < 1")
            return when (nDays) {
                1 -> forDaily()
                7 -> forWeekly(baseDate.dayOfWeek)
                else -> EveryNDays(baseDate, nDays)
            }
        }

        /** Returns a DatePattern for 'every [dayOfWeek]'. Ex every Tuesday */
        @JvmStatic
        fun forWeekly(dayOfWeek: DayOfWeek): DatePattern = EveryWeekday(dayOfWeek)

        /**
         * Returns a DatePattern for '[n]th day of every month' Ex: 4th of every month.
         * If n > #days for some month, truncates to last day of that month.
         * 1 <= n <= 31
         */
        @JvmStatic
        fun forMonthly(@IntRange(from = 1, to = 31) n: Int): DatePattern = NthDayOfEveryMonth(n)

        /** Returns a DatePattern for 'once every [monthDay]' Ex: Once every January 3rd */
        @JvmStatic
        fun forAnnual(monthDay: MonthDay): DatePattern = EveryMonthDay(monthDay)

        /** Returns a DatePattern for '[n]th [dayOfWeek] every month' Ex: 3rd Friday every month */
        private fun forNthWeekdayOfEveryMonth(dayOfWeek: DayOfWeek, n: Int): DatePattern =
            TODO() // edge case of n = 5
    }

    /* Implementations */

    private object DailyPattern: DatePattern {
        override fun nextOccurrenceFrom(date: LocalDate) = date
    }

    /** Once every [nDays], starting from [baseDate] and on */
    private class EveryNDays(
        private val baseDate: LocalDate,
        @IntRange(from = 1) private val nDays: Int = 1,
    ): DatePattern {
        override fun nextOccurrenceFrom(date: LocalDate): LocalDate {
            val numDaysApart = ChronoUnit.DAYS.between(baseDate, date)
            // next val is current + complement of modulus
            val numDaysToAdd = Period.ofDays((nDays - numDaysApart.mod(nDays)).mod(nDays))
            return date.plus(numDaysToAdd)
        }
    }

    private class EveryWeekday(dayOfWeek: DayOfWeek): DatePattern {
        private val dayOfWeekTemporalAdjuster = nextOrSame(dayOfWeek)
        override fun nextOccurrenceFrom(date: LocalDate): LocalDate = date.with(dayOfWeekTemporalAdjuster)
    }

    private class NthDayOfEveryMonth(val n: Int): DatePattern {
        override fun nextOccurrenceFrom(date: LocalDate): LocalDate = when {
            // if #days in month < n, last day of current month
            (date.lengthOfMonth() < n) -> date.with(lastDayOfMonth())
            // if date.day <= n, just move up to day n
            (date.dayOfMonth <= n) -> date.withDayOfMonth(n)
            // else nextMonth this day
            else -> nextOccurrenceFrom(date.with(firstDayOfNextMonth()))
        }
    }

    private class EveryMonthDay(val monthDay: MonthDay): DatePattern {
        override fun nextOccurrenceFrom(date: LocalDate): LocalDate =
            monthDay.atYear(date.year + if (MonthDay.from(date).isAfter(monthDay)) 1 else 0)
    }

}
