package com.fit5046.health304

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fit5046.health304.sensors.SensorCsvReader
import com.fit5046.health304.sensors.SensorDataSimulator
import com.fit5046.health304.sensors.SimulatedSensorReading
import com.fit5046.health304.sensors.StepsMode
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class SensorSimulatorTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun realAssetWrapsAndStopCancelsReplayWithoutDuplicateStart() {
        val samples = SensorCsvReader.read(context.assets.open("sensor_data.csv").reader())
        assertEquals(120, samples.size)
        val rows = CopyOnWriteArrayList<SimulatedSensorReading>()
        val enough = CountDownLatch(samples.size + 2)
        val simulator = SensorDataSimulator(context, intervalMillis = 10)
        try {
            simulator.start("test-account-a", StepsMode.INTERVAL) { rows.add(it); enough.countDown() }
            simulator.start("test-account-a", StepsMode.INTERVAL) { fail("Duplicate loop started") }
            assertTrue(enough.await(10, TimeUnit.SECONDS))
            simulator.stop()
            assertEquals(samples[0].heartRate, rows[0].heartRate)
            assertEquals(samples[0].heartRate, rows[120].heartRate)
            assertEquals(samples[1].stepsInterval, rows[121].steps)
            assertTrue(rows.all { it.userId == "test-account-a" && it.timestamp > 0 })
        } finally { simulator.stop() }
    }

    @Test fun changingAccountDoesNotReusePreviousUid() {
        val a = CountDownLatch(1)
        val b = CountDownLatch(1)
        val simulator = SensorDataSimulator(context, intervalMillis = 10)
        try {
            simulator.start("test-account-a", StepsMode.INTERVAL) { assertEquals("test-account-a", it.userId); a.countDown() }
            assertTrue(a.await(3, TimeUnit.SECONDS))
            simulator.start("test-account-b", StepsMode.CUMULATIVE) { assertEquals("test-account-b", it.userId); b.countDown() }
            assertTrue(b.await(3, TimeUnit.SECONDS))
        } finally { simulator.stop() }
    }
}
