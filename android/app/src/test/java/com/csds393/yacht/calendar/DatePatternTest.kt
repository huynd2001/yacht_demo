package com.csds393.yacht.calendar

import org.junit.Assert.*

import org.junit.Test
import java.time.*

class DatePatternTest {

    /** For tests that need a diverse list of dates, without any particular conditions */
    private val arbitraryDates = listOf(
        LocalDate.now(),
        LocalDate.ofYearDay(1923, 42),
        LocalDate.ofYearDay(2017, 1),
        LocalDate.ofYearDay(2020, 366),
        LocalDate.ofYearDay(2021, 365),
        LocalDate.ofYearDay(2023, 159),
        LocalDate.ofYearDay(2024, 60),
        LocalDate.ofYearDay(4324, 23),
    )

    /** Range of years that includes a leap year */
    private val years = 2000..2004

    @Test
    fun forDaily() {
        // whatever the input date, should return it
        val dailyPattern = DatePattern.forDaily()
        for (date in arbitraryDates) {
            assertEquals(date, dailyPattern.nextOccurrenceFrom(date))
            assertEquals(date.plusDays(1), dailyPattern.nextOccurrenceAfter(date))
        }
    }

    @Test
    fun `forEveryNDaysFrom, with n ranging from 2 to 8`() {
        val baseDate = LocalDate.of(2021, Month.OCTOBER, 20)
        val minN = 2
        val maxN = 8
        for (intN in minN..maxN) {
            val n: Long = intN.toLong()
            val pattern = DatePattern.forEveryNDaysFrom(baseDate, intN)
            // next occurrence from today is today
            assertEquals(baseDate, pattern.nextOccurrenceFrom(baseDate))
            // next occurrence after today is today+n
            assertEquals(baseDate.plusDays(n), pattern.nextOccurrenceAfter(baseDate))
            for (j in 1 until n) {
                // next occurrence from date-[1, n) is date
                assertEquals(baseDate, pattern.nextOccurrenceFrom(baseDate.minusDays(j)))
                // next occurrence from date-[n, n+n) is date - n
                assertEquals(baseDate.minusDays(n), pattern.nextOccurrenceFrom(baseDate.minusDays(n + j)))
            }
        }
    }

    @Test
    fun `forWeekly before baseDate`() {
        for (arbitraryDate in arbitraryDates) for (day in DayOfWeek.values()) {
            val weeklyPattern = DatePattern.forWeekly(day)
            val baseDate = arbitraryDate.with(day)
            // next occurrence from 0-6 days before baseDate should be baseDate
            for (daysBefore in 0..6L)
                assertEquals(baseDate, weeklyPattern.nextOccurrenceFrom(baseDate.minusDays(daysBefore)))
        }
    }

    @Test
    fun `forWeekly after baseDate`() {
        for (arbitraryDate in arbitraryDates) for (day in DayOfWeek.values()) {
            val weeklyPattern = DatePattern.forWeekly(day)
            val baseDate = arbitraryDate.with(day)
            // next occurrence after date should be base+7 days
            assertEquals(baseDate.plusDays(7), weeklyPattern.nextOccurrenceAfter(baseDate))
        }
    }

    @Test
    fun `forMonthly n=30, should be last day of feb and 30th of other months`() {
        val monthlyPattern = DatePattern.forMonthly(30)
        for (year in years) for (month in Month.values()) {
            val baseDate = LocalDate.of(year, month, 1)
            val monthDay = MonthDay.of(month,  if (month == Month.FEBRUARY) 29 else 30)
            val correctDate = monthDay.atYear(year)
            assertEquals(correctDate, monthlyPattern.nextOccurrenceFrom(baseDate))
        }
    }

    @Test
    fun `forMonthly n=31 should be lastDay of every month`() {
        val monthlyPattern = DatePattern.forMonthly(31)
        for (year in years) for (month in Month.values()) {
            val baseDate = LocalDate.of(year, month, 1)
            val monthDay = MonthDay.of(month, month.maxLength())
            val lastDay = monthDay.atYear(year)
            assertEquals(lastDay, monthlyPattern.nextOccurrenceFrom(baseDate))
        }
    }

    @Test
    fun `forAnnual February 29`() {
        val monthDay = MonthDay.of(Month.FEBRUARY, 29)
        val annualPattern = DatePattern.forAnnual(monthDay)
        for (year in years) {
            val baseDate = LocalDate.of(year, Month.JANUARY, 1)
            assertEquals(monthDay.atYear(year), annualPattern.nextOccurrenceFrom(baseDate))
        }
    }

    @Test
    fun `forAnnual baseDate before monthDay in same month`() {
        val monthDay = MonthDay.of(Month.JANUARY, 16)
        val annualPattern = DatePattern.forAnnual(monthDay)
        for (year in years) {
            // date before target monthDay, same month
            val baseDate = LocalDate.of(year, Month.JANUARY, 2)
            // should return date in this year
            assertEquals(monthDay.atYear(year), annualPattern.nextOccurrenceFrom(baseDate))
        }
    }

    @Test
    fun `forAnnual baseDate after monthDay in middle of month`() {
        val monthDay = MonthDay.of(Month.JANUARY, 16)
        val annualPattern = DatePattern.forAnnual(monthDay)
        for (year in years) {
            // date 1 day after target monthDay
            val baseDate = LocalDate.of(year, Month.JANUARY, 17)
            // should return next year
            assertEquals(monthDay.atYear(year+1), annualPattern.nextOccurrenceFrom(baseDate))
        }
    }

    @Test
    fun `forAnnual baseDate after monthDay in beginning of month`() {
        val monthDay = MonthDay.of(Month.JANUARY, 1)
        val annualPattern = DatePattern.forAnnual(monthDay)
        for (year in years) {
            // date 1 day after target monthDay
            val baseDate = LocalDate.of(year, Month.JANUARY, 17)
            // should return next year
            assertEquals(monthDay.atYear(year+1), annualPattern.nextOccurrenceFrom(baseDate))
        }
    }
}
