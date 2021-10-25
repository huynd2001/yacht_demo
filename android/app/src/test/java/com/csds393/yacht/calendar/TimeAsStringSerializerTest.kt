package com.csds393.yacht.calendar

import com.csds393.yacht.AbstractSerializationTest
import java.time.LocalTime

private fun LocalTime.toStringTimePair() = Pair(this.toString(), this)

class TimeAsStringSerializerTest : AbstractSerializationTest<LocalTime>(
    mapOf(
        LocalTime.of(23, 0).toStringTimePair(),
        LocalTime.of(12, 42).toStringTimePair(),
        LocalTime.of(21, 22, 0).toStringTimePair(),
        LocalTime.of(3, 59, 53).toStringTimePair(),
        LocalTime.of(14, 11, 36, 0).toStringTimePair(),
        LocalTime.of(14, 11, 36, 21).toStringTimePair(),
    ),
    TimeAsStringSerializer
)
