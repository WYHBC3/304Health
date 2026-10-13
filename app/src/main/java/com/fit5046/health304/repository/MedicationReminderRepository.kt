package com.fit5046.health304.repository

import android.app.Application
import com.fit5046.health304.data.AppDatabase
import com.fit5046.health304.data.MedicationReminder

class MedicationReminderRepository(application: Application) {
    private val dao = AppDatabase.getDatabase(application).medicationReminderDAO()
    fun getReminders(userId: String) = dao.getReminders(userId)
    suspend fun insert(reminder: MedicationReminder) = dao.insert(reminder)
    suspend fun update(reminder: MedicationReminder) = dao.update(
        reminder.id, reminder.userId, reminder.medicationName,
        reminder.hour, reminder.minute, reminder.reminderEnabled
    )
    suspend fun delete(id: Long, userId: String) = dao.delete(id, userId)
}
