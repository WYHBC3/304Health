package com.fit5046.health304

import com.fit5046.health304.reminders.nextReminderTime
import com.fit5046.health304.sensors.SensorCsvReader
import com.fit5046.health304.sensors.StepsMode
import org.junit.Assert.*
import org.junit.Test
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class D3LogicTest {
    private fun time(text: String, zone: String = "UTC"): Long =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply {
            timeZone = TimeZone.getTimeZone(zone)
        }.parse(text)!!.time

    @Test fun nextAlarmUsesTodayBeforeTimeAndTomorrowAfterTime() {
        val zone = TimeZone.getTimeZone("UTC")
        assertEquals(time("2026-10-14 09:00"), nextReminderTime(9, 0, time("2026-10-14 08:59"), zone))
        assertEquals(time("2026-10-15 09:00"), nextReminderTime(9, 0, time("2026-10-14 09:00"), zone))
        assertEquals(time("2027-01-01 08:00"), nextReminderTime(8, 0, time("2026-12-31 23:59"), zone))
    }

    @Test fun daylightSavingKeepsLocalHourRatherThanAdding24Hours() {
        val zone = TimeZone.getTimeZone("Australia/Melbourne")
        val expected = time("2026-10-04 08:00", zone.id)
        assertEquals(expected, nextReminderTime(8, 0, time("2026-10-03 08:01", zone.id), zone))
    }

    @Test fun csvKeepsIntervalAndCumulativeStepsSeparate() {
        val rows = SensorCsvReader.read(StringReader("sourceTime,heartRate,stepsInterval,stepsCumulative\n2016-04-12T07:21:00,102,17,17\n2016-04-12T07:22:00,88,9,26\n"))
        assertEquals(2, rows.size)
        assertEquals(9, rows[1].steps(StepsMode.INTERVAL))
        assertEquals(26, rows[1].steps(StepsMode.CUMULATIVE))
    }

    @Test(expected = IllegalArgumentException::class)
    fun emptyCsvDoesNotStartAFakeSimulation() {
        SensorCsvReader.read(StringReader("sourceTime,heartRate,stepsInterval,stepsCumulative\n"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeStepsAreRejected() {
        SensorCsvReader.read(StringReader("sourceTime,heartRate,stepsInterval,stepsCumulative\n2016-04-12T07:21:00,102,-1,17\n"))
    }
}
