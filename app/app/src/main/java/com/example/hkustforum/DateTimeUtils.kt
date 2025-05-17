/*  util/DateTimeUtils.kt  */
package com.example.hkustforum

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

object DateTimeUtils {

    /** Accepts either ISO strings **with or without** an offset. */
    fun formatRelative(raw: String): String {
        val instant: Instant = try {
            /* 1️⃣ with offset, e.g. 2025-05-08T02:07:13+08:00 or …Z */
            OffsetDateTime.parse(raw, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()
        } catch (_: DateTimeParseException) {
            try {
                /* 2️⃣ without offset, assume local zone */
                LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            } catch (_: DateTimeParseException) {
                return raw          // still can’t parse → show as-is
            }
        }

        val now   = Instant.now()
        val mins  = ChronoUnit.MINUTES.between(instant, now)
        val hours = ChronoUnit.HOURS  .between(instant, now)
        val days  = ChronoUnit.DAYS   .between(instant, now)

        return when {
            mins  < 1  -> "just now"
            mins  < 60 -> "${mins} m ago"
            hours < 24 -> "${hours} h ago"
            days  == 1L-> "yesterday"
            days  <  7 -> "${days} d ago"
            else       -> LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                .toLocalDate()
                .toString()          // 2025-05-08
        }
    }
}
