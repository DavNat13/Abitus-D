package com.abitusd.app.feature.train.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/** One all-day planned workout exported to a calendar app. */
data class IcsEvent(
    val uid: String,
    val date: LocalDate,
    val summary: String,
)

/** Builds an RFC 5545 calendar with all-day events. */
object IcsBuilder {
    private val dateFormat = DateTimeFormatter.BASIC_ISO_DATE
    private val stampFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC)

    fun build(
        events: List<IcsEvent>,
        now: Instant = Instant.now(),
    ): String =
        buildString {
            line("BEGIN:VCALENDAR")
            line("VERSION:2.0")
            line("PRODID:-//Abitus-D//Planning//EN")
            events.forEach { e ->
                line("BEGIN:VEVENT")
                line("UID:${e.uid}@abitusd")
                line("DTSTAMP:${stampFormat.format(now)}")
                line("DTSTART;VALUE=DATE:${dateFormat.format(e.date)}")
                line("DTEND;VALUE=DATE:${dateFormat.format(e.date.plusDays(1))}")
                line("SUMMARY:${escape(e.summary)}")
                line("END:VEVENT")
            }
            line("END:VCALENDAR")
        }

    private fun StringBuilder.line(text: String) {
        append(text).append("\r\n")
    }

    private fun escape(text: String) =
        text
            .replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace("\n", "\\n")
}
