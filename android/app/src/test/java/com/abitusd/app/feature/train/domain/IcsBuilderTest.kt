package com.abitusd.app.feature.train.domain

import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class IcsBuilderTest {
    private val ics =
        IcsBuilder.build(
            listOf(IcsEvent("abc", LocalDate.of(2026, 11, 30), "Push, A; B")),
            Instant.parse("2026-11-22T10:00:00Z"),
        )

    @Test
    fun allDayEventEndsNextDay() {
        assertTrue(ics.contains("DTSTART;VALUE=DATE:20261130\r\n"))
        assertTrue(ics.contains("DTEND;VALUE=DATE:20261201\r\n"))
        assertTrue(ics.contains("DTSTAMP:20261122T100000Z\r\n"))
    }

    @Test
    fun summaryIsEscaped() {
        assertTrue(ics.contains("SUMMARY:Push\\, A\\; B\r\n"))
        assertTrue(ics.startsWith("BEGIN:VCALENDAR\r\n") && ics.endsWith("END:VCALENDAR\r\n"))
    }
}
