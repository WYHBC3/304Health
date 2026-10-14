package com.fit5046.health304.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fit5046.health304.context.ContextRules
import com.fit5046.health304.context.HealthContext
import com.fit5046.health304.data.Recommendation
import com.fit5046.health304.repository.ContextRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import kotlin.math.roundToInt

/**
 * Advanced feature 2: WorkManager background task.
 * Aggregates sensory + non-sensory data, applies the context rules,
 * and saves the personalised recommendation to Room.
 */
class ContextAwareWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Log.d(TAG, "No signed-in user, skipping")
            return Result.success()
        }

        return try {
            val repository = ContextRepository(applicationContext)

            // 1. Collect context data
            val readings = repository.getRecentReadings(uid)
            val preferences = repository.getPreferences(uid)
            val latestMood = repository.getLatestMood(uid)

            // 2. Aggregate sensory data with non-sensory data
            val context = HealthContext(
                avgHeartRate = readings.takeIf { it.isNotEmpty() }
                    ?.map { it.heartRate }?.average()?.roundToInt(),
                avgStepsPerMinute = readings.takeIf { it.isNotEmpty() }
                    ?.map { it.steps }?.average()?.roundToInt(),
                healthGoal = preferences?.healthGoal,
                activityLevel = preferences?.activityLevel,
                latestMood = latestMood?.mood,
                hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            )
            Log.d(TAG, "Context built from ${readings.size} sensor readings: $context")

            // 3. Apply the rules and save the result
            val suggestion = ContextRules.recommend(context)
            repository.saveRecommendation(
                Recommendation(
                    userId = uid,
                    title = suggestion.title,
                    message = suggestion.message,
                    category = suggestion.category,
                    reason = suggestion.reason
                )
            )
            Log.d(TAG, "Recommendation saved: ${suggestion.title} [${suggestion.category}]")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Context-aware work failed, will retry", e)
            Result.retry()
        }
    }

    companion object {
        const val TAG = "ContextAwareWorker"
    }
}