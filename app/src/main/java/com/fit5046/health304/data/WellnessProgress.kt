package com.fit5046.health304.data

import androidx.room.Entity

/**
 * Stores daily wellness completion state for one signed-in user.
 *
 * Mood completion is not stored here because it is calculated directly
 * from today's MoodEntry records.
 *
 * userId and dateKey together identify one daily progress record.
 */
@Entity(
    tableName = "wellness_progress",
    primaryKeys = ["userId", "dateKey"]
)
data class WellnessProgress(

    val userId: String,

    // Example: 2026-10-15
    val dateKey: String,

    val medicationCompleted: Boolean = false,

    val exerciseCompleted: Boolean = false,

    val updatedAt: Long = System.currentTimeMillis()
)