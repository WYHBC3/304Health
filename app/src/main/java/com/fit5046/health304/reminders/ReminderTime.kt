package com.fit5046.health304.reminders

import java.util.Calendar
import java.util.TimeZone

/** Next daily local time, using calendar days rather than fixed 24-hour durations (DST). */
fun nextReminderTime(hour: Int, minute: Int, now: Long = System.currentTimeMillis(),
                     timeZone: TimeZone = TimeZone.getDefault()): Long {
    require(hour in 0..23 && minute in 0..59)
    return Calendar.getInstance(timeZone).apply {
        timeInMillis = now
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis <= now) add(Calendar.DAY_OF_YEAR, 1)
    }.timeInMillis
}
