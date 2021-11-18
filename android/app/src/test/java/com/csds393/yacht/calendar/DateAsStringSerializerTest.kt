package com.csds393.yacht.calendar

import com.csds393.yacht.AbstractSerializationTest
import java.time.LocalDate

private fun LocalDate.toStringDatePair() = Pair(this.toString(), this)

class DateAsStringSerializerTest : AbstractSerializationTest<LocalDate>(
    mapOf(
        LocalDate.of(1, 1, 1).toStringDatePair(),
        LocalDate.of(2012, 12, 31).toStringDatePair(),
        LocalDate.of(2030, 5, 12).toStringDatePair(),
    ),
    DateAsStringSerializer
)
