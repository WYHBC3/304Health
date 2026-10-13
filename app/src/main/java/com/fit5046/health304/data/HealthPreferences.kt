package com.fit5046.health304.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One preference record per Firebase account. DatePicker stores dates in UTC. */
@Entity(tableName = "health_preferences")
data class HealthPreferences(
    @PrimaryKey val userId: String,
    val dateOfBirthMillis: Long,
    val healthGoal: String,
    val activityLevel: String,
    val updatedAt: Long = System.currentTimeMillis()
)
