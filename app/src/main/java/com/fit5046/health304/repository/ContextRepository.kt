package com.fit5046.health304.repository

import android.content.Context
import com.fit5046.health304.data.AppDatabase
import com.fit5046.health304.data.HealthPreferences
import com.fit5046.health304.data.MoodEntry
import com.fit5046.health304.data.Recommendation
import com.fit5046.health304.data.SensorReading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Collects all context data from Room for the context-aware worker.
 * Takes a Context (not Application) because a Worker only has a Context.
 */
class ContextRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)

    suspend fun getRecentReadings(userId: String, limit: Int = 12): List<SensorReading> =
        db.sensorReadingDAO().getLatest(userId, limit)

    suspend fun getPreferences(userId: String): HealthPreferences? =
        db.healthPreferencesDAO().getPreferences(userId).first()

    // Mood entries are already ordered newest first
    suspend fun getLatestMood(userId: String): MoodEntry? =
        db.moodDAO().getMoodEntries(userId).first().firstOrNull()

    suspend fun saveRecommendation(recommendation: Recommendation) =
        db.recommendationDAO().save(recommendation)

    fun observeRecommendation(userId: String): Flow<Recommendation?> =
        db.recommendationDAO().observe(userId)
}