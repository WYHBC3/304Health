package com.fit5046.health304.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One simulated wearable reading (sensory data).
 * Rows are inserted by SensorDataSimulator from a real Fitbit dataset (Kaggle).
 */
@Entity(tableName = "sensor_readings", indices = [Index("userId")])
data class SensorReading(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val timestamp: Long,
    val heartRate: Int,     // beats per minute
    val steps: Int          // steps in the last minute
)