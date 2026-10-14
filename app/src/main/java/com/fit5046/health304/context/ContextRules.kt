package com.fit5046.health304.context

/** Everything the rules look at: sensory data + non-sensory data. */
data class HealthContext(
    // Sensory data (simulated wearable, real Fitbit dataset)
    val avgHeartRate: Int?,
    val avgStepsPerMinute: Int?,
    // Non-sensory data
    val healthGoal: String?,      // Health Preferences (P2)
    val activityLevel: String?,   // Health Preferences (P2)
    val latestMood: String?,      // Mood Diary (P3)
    val hourOfDay: Int            // time of day
)

data class Suggestion(
    val title: String,
    val message: String,
    val category: String,
    val reason: String
)

/**
 * Rule-based context-aware personalisation.
 * Rules are checked from top to bottom; the first rule that matches wins,
 * so safety-related rules (high heart rate) come first.
 */
object ContextRules {

    private val negativeMoods = setOf("Stressed", "Low")

    fun recommend(c: HealthContext): Suggestion {
        // Personalise duration and difficulty using the user's activity level
        val minutes = when (c.activityLevel) {
            "High" -> 30
            "Moderate" -> 20
            else -> 10
        }
        val level = when (c.activityLevel) {
            "High" -> "advanced"
            "Moderate" -> "intermediate"
            else -> "beginner"
        }
        val reason = buildReason(c)

        return when {
            // Rule 1: heart rate is high while the user is barely moving
            c.avgHeartRate != null && c.avgHeartRate > 100 &&
                    c.avgStepsPerMinute != null && c.avgStepsPerMinute < 10 ->
                Suggestion(
                    "Take a breather",
                    "Your heart rate is elevated while you are resting. Try 5 minutes of slow breathing before any exercise.",
                    "Recovery", reason
                )

            // Rule 2: the latest mood entry is negative
            c.latestMood in negativeMoods ->
                Suggestion(
                    "Calm your mind",
                    "You recently felt ${c.latestMood?.lowercase()}. A $minutes-minute gentle yoga or stretching session can help you relax.",
                    "Mindfulness", reason
                )

            // Rule 3: late evening or night
            c.hourOfDay >= 21 || c.hourOfDay < 6 ->
                Suggestion(
                    "Wind down for sleep",
                    "It's late. 5–10 minutes of light stretching is better than intense exercise before bed.",
                    "Relaxation", reason
                )

            // Rule 4: the user has been very active recently
            c.avgStepsPerMinute != null && c.avgStepsPerMinute >= 60 ->
                Suggestion(
                    "Time to recover",
                    "You have been very active. Cool down with $minutes minutes of stretching and drink some water.",
                    "Recovery", reason
                )

            // Rule 5: inactive recently and the goal is fitness
            c.avgStepsPerMinute != null && c.avgStepsPerMinute < 10 &&
                    c.healthGoal == "Improve fitness" ->
                Suggestion(
                    "Get moving",
                    "You have been inactive recently. Try a $minutes-minute $level home cardio workout.",
                    "Cardio", reason
                )

            // Rule 6: fall back to the user's health goal
            c.healthGoal == "Manage stress" ->
                Suggestion(
                    "Mindful movement",
                    "Try a $minutes-minute $level yoga flow to keep stress levels down.",
                    "Mindfulness", reason
                )

            c.healthGoal == "Improve sleep" ->
                Suggestion(
                    "Move during the day",
                    "A $minutes-minute walk during the day can help you sleep better tonight.",
                    "Light activity", reason
                )

            else ->
                Suggestion(
                    "Keep the habit going",
                    "A $minutes-minute $level full-body workout keeps your routine on track.",
                    "Strength", reason
                )
        }
    }

    // Short explanation shown on the card, so the user can see why this was suggested
    private fun buildReason(c: HealthContext): String {
        val parts = mutableListOf<String>()
        if (c.avgHeartRate != null) parts.add("Heart rate ${c.avgHeartRate} bpm")
        if (c.avgStepsPerMinute != null) parts.add("${c.avgStepsPerMinute} steps/min")
        if (c.avgHeartRate == null) parts.add("No sensor data yet")
        c.healthGoal?.let { parts.add("Goal: $it") }
        c.activityLevel?.let { parts.add("Activity: $it") }
        c.latestMood?.let { parts.add("Mood: $it") }
        return parts.joinToString(" · ")
    }
}