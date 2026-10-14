package com.fit5046.health304.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationReminderDAO {
    @Query("SELECT * FROM medication_reminders WHERE userId = :userId ORDER BY hour, minute, id")
    fun getReminders(userId: String): Flow<List<MedicationReminder>>

    @Query("SELECT * FROM medication_reminders WHERE id = :id AND userId = :userId")
    suspend fun getReminder(id: Long, userId: String): MedicationReminder?

    @Insert
    suspend fun insert(reminder: MedicationReminder): Long

    // Ownership is checked in SQL, including for edit/delete operations.
    @Query("UPDATE medication_reminders SET medicationName = :name, hour = :hour, minute = :minute, reminderEnabled = :enabled WHERE id = :id AND userId = :userId")
    suspend fun update(id: Long, userId: String, name: String, hour: Int, minute: Int, enabled: Boolean): Int

    @Query("DELETE FROM medication_reminders WHERE id = :id AND userId = :userId")
    suspend fun delete(id: Long, userId: String): Int
}
