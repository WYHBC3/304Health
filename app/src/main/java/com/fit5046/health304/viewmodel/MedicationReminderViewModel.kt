package com.fit5046.health304.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.fit5046.health304.data.MedicationReminder
import com.fit5046.health304.repository.MedicationReminderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class MedicationReminderViewModel(application: Application) : AccountDataViewModel(application) {
    private val repository = MedicationReminderRepository(application)
    val reminders = userId.flatMapLatest { uid ->
        if (uid == null) flowOf(emptyList()) else repository.getReminders(uid)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    suspend fun save(id: Long?, name: String, hour: Int, minute: Int, enabled: Boolean) {
        require(name.isNotBlank()) { "Enter a medication name." }
        require(hour in 0..23 && minute in 0..59) { "Choose a valid reminder time." }
        val reminder = MedicationReminder(id ?: 0, requireUserId(), name.trim(), hour, minute, enabled)
        if (id == null) repository.insert(reminder)
        else check(repository.update(reminder) == 1) { "This reminder is no longer available." }
    }

    suspend fun delete(id: Long) {
        check(repository.delete(id, requireUserId()) == 1) { "This reminder is no longer available." }
    }
}
