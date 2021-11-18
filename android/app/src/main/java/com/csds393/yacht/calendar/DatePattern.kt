package com.csds393.yacht.calendar

import androidx.annotation.IntRange
import androidx.room.TypeConverter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.MonthDay
import java.time.temporal.TemporalAdjusters.*
import kotlin.IllegalArgumentException

/**
 * A pattern for Dates.
 * Ex: Every Monday, every 3 days, 2nd day of every month, every June 6th
 */
sealed interface DatePattern {

    /** Returns the soonest applicable date strictly after [date] */
    fun nextOccurrenceAfter(date: LocalDate) = nextOccurrenceFrom(date.plusDays(1))

    /** Returns the soonest applicable date on or after [date] */
    fun nextOccurrenceFrom(date: LocalDate): LocalDate

    /**
     * Returns the list of dates contained in both [queryWindow]
     * and [activeWindow] that fit this pattern
     */
    fun getOccurrencesInWindows(
        queryWindow: ClosedRange<LocalDate>,
        activeWindow: ClosedRange<LocalDate>
    ): List<LocalDate> {
        val dates: MutableList<LocalDate> = mutableListOf()
        // first possible occurrence after each window's start dates
        var nextOccurrence = maxOf(queryWindow.start, activeWindow.start)

        val latestDate = minOf(queryWindow.endInclusive, activeWindow.endInclusive)
        // keep adding while valid
        while (latestDate.isAfter(nextOccurrence)) {
            dates.add(nextOccurrence)
            nextOccurrence = nextOccurrenceAfter(nextOccurrence)
        }
        return dates
    }

    companion object { // factory methods
        /** Returns a DatePattern for 'once every [nDays], before, on, and after to [baseDate]' */
        @JvmStatic
        fun forEveryNDaysFrom(
            baseDate: LocalDate,
            @IntRange(from = 1, to = (1L shl PREFIX_BIT_POS)-1)
            nDays: Int,
        ): DatePattern {
            if (nDays < 1) throw IllegalArgumentException("$nDays is < 1")
            return when (nDays) {
                7 -> forWeekly(baseDate.dayOfWeek)
                else -> EveryNDays(baseDate.toEpochDay(), nDays)
            }
        }
        /** Returns a DatePattern for 'every [dayOfWeek]'. Ex every Tuesday */
        @JvmStatic
        fun forWeekly(dayOfWeek: DayOfWeek): DatePattern = Weekly(dayOfWeek)

        /**
         * Returns a DatePattern for '[n]th day of every month' Ex: 4th of every month.
         * If n > #days for some month, truncates to last day of that month.
         * 1 <= n <= 31
         */
        @JvmStatic
        fun forMonthly(@IntRange(from = 1, to = 31) n: Int): DatePattern = Monthly(n)

        /** Returns a DatePattern for 'once every [monthDay]' Ex: Once every January 3rd */
        @JvmStatic
        fun forAnnual(monthDay: MonthDay): DatePattern = Annual(monthDay)

        /** Returns a DatePattern for '[n]th [dayOfWeek] every month' Ex: 3rd Friday every month */
        private fun forNthWeekdayOfEveryMonth(dayOfWeek: DayOfWeek, n: Int): DatePattern =
            TODO() // edge case of n = 5

        /* TypeConverters for Room database */
        private const val PREFIX_BIT_POS = 64-8

        private const val N_DAYS_PATTERN_PREFIX = 1L
        private const val WEEKLY_PATTERN_PREFIX = 2L
        private const val MONTHLY_PATTERN_PREFIX = 3L
        private const val ANNUAL_PATTERN_PREFIX = 4L

        @TypeConverter
        @JvmStatic
        fun datePatternToInt(value: DatePattern): Long = when (value) {
            is EveryNDays -> (N_DAYS_PATTERN_PREFIX shl PREFIX_BIT_POS) +
                    (value.nDays shl 32) + value.offsetFromEpoch
            is Weekly -> (WEEKLY_PATTERN_PREFIX shl PREFIX_BIT_POS) + value.dayOfWeek.value
            is Monthly -> (MONTHLY_PATTERN_PREFIX shl PREFIX_BIT_POS) + value.dayOfMonth
            is Annual ->
                (ANNUAL_PATTERN_PREFIX shl PREFIX_BIT_POS) +
                        (value.monthDay.monthValue shl 32) +
                        value.monthDay.dayOfMonth
        }
        /*
        6 types
        MSByte for type
        */
        @TypeConverter
        @JvmStatic
        fun intToDatePattern(value: Long): DatePattern = when (value ushr PREFIX_BIT_POS) {
            N_DAYS_PATTERN_PREFIX -> EveryNDays(value and 0xffff_ffff, (value ushr 32 and 0x00ff_ffff).toInt())
            WEEKLY_PATTERN_PREFIX -> Weekly(DayOfWeek.of((value and 0x00ff).toInt()))
            MONTHLY_PATTERN_PREFIX -> Monthly((value and 0xffff).toInt())
            ANNUAL_PATTERN_PREFIX -> Annual(MonthDay.of((value ushr 32 and 0x00ff_ffff).toInt(), (value and 0xffff_ffff).toInt()))
            else -> throw IllegalArgumentException()
        }

    }

    /* Implementations */
    /** Once every [nDays], starting from baseDate and on */
    private class EveryNDays(
        val offsetFromEpoch: Long,
        @IntRange(from = 1) val nDays: Int,
    ): DatePattern {

        override fun nextOccurrenceFrom(date: LocalDate): LocalDate {
            val numDaysApart = offsetFromEpoch - date.toEpochDay()
            // next val is current + complement of modulus
            val numDaysToAdd = (nDays - numDaysApart.mod(nDays)).mod(nDays)
            return date.plusDays(numDaysToAdd.toLong())
        }
    }

    private class Weekly(val dayOfWeek: DayOfWeek): DatePattern {
        private val dayOfWeekTemporalAdjuster = nextOrSame(dayOfWeek)
        override fun nextOccurrenceFrom(date: LocalDate): LocalDate {
            return date.with(dayOfWeekTemporalAdjuster)
        }
    }

    private class Monthly(@IntRange(from = 1L, to = 31L) val dayOfMonth: Int): DatePattern {
        override fun nextOccurrenceFrom(date: LocalDate): LocalDate = when {
            // if #days in month < n, last day of current month
            (date.lengthOfMonth() < dayOfMonth) -> date.with(lastDayOfMonth())
            // if date.day <= n, just move up to day n
            (date.dayOfMonth <= dayOfMonth) -> date.withDayOfMonth(dayOfMonth)
            // else nextMonth this day
            else -> nextOccurrenceFrom(date.with(firstDayOfNextMonth()))
        }
    }

    private class Annual(val monthDay: MonthDay): DatePattern {
        override fun nextOccurrenceFrom(date: LocalDate): LocalDate =
            monthDay.atYear(date.year + if (MonthDay.from(date).isAfter(monthDay)) 1 else 0)
    }

}
