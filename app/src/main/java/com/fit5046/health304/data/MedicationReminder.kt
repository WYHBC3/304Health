package com.fit5046.health304.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "medication_reminders", indices = [Index("userId")])
data class MedicationReminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val medicationName: String,
    val hour: Int,
    val minute: Int,
    val reminderEnabled: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
