package com.fit5046.health304.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local mood diary record for a signed-in user.
 *
 * Each record is linked to a Firebase Authentication UID through userId,
 * so users can only access their own mood history.
 */
@Entity(tableName = "mood_entries")
data class MoodEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: String,

    val dateMillis: Long,

    val mood: String,

    val factors: String,

    val notes: String
)