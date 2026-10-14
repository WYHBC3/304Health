package com.fit5046.health304.sensors

import java.io.Reader

enum class StepsMode { INTERVAL, CUMULATIVE }

data class SensorSample(val sourceTime: String, val heartRate: Int,
                        val stepsInterval: Int, val stepsCumulative: Int) {
    fun steps(mode: StepsMode) = if (mode == StepsMode.INTERVAL) stepsInterval else stepsCumulative
}

/** Simple, deliberately strict parser for our generated four-column CSV asset. */
object SensorCsvReader {
    fun read(reader: Reader): List<SensorSample> = reader.buffered().use { input ->
        require(input.readLine()?.trim()?.removePrefix("\uFEFF") == "sourceTime,heartRate,stepsInterval,stepsCumulative") {
            "Unexpected sensor CSV columns."
        }
        input.lineSequence().filter { it.isNotBlank() }.mapIndexed { index, line ->
            val fields = line.split(',').map(String::trim)
            require(fields.size == 4) { "Invalid sensor row ${index + 2}." }
            val sample = SensorSample(fields[0], fields[1].toInt(), fields[2].toInt(), fields[3].toInt())
            require(sample.sourceTime.isNotBlank() && sample.heartRate > 0 &&
                sample.stepsInterval >= 0 && sample.stepsCumulative >= 0) { "Invalid sensor values at row ${index + 2}." }
            sample
        }.toList().also { require(it.isNotEmpty()) { "Sensor CSV has no readings." } }
    }
}
