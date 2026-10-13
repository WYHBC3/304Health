package com.fit5046.health304.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.fit5046.health304.data.HealthPreferences
import com.fit5046.health304.repository.HealthPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class HealthPreferencesViewModel(application: Application) : AccountDataViewModel(application) {
    private val repository = HealthPreferencesRepository(application)
    val preferences = userId.flatMapLatest { uid ->
        if (uid == null) flowOf(null) else repository.getPreferences(uid)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    suspend fun save(dateOfBirthMillis: Long, healthGoal: String, activityLevel: String) {
        require(dateOfBirthMillis <= System.currentTimeMillis()) { "Choose a valid date of birth." }
        require(healthGoal.isNotBlank() && activityLevel.isNotBlank()) { "Complete all required fields." }
        repository.save(HealthPreferences(requireUserId(), dateOfBirthMillis, healthGoal, activityLevel))
    }
}
