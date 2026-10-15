package com.fit5046.health304.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.fit5046.health304.data.AppDatabase
import com.fit5046.health304.data.SensorReading
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

/** Live simulated wearable readings for the signed-in user (newest first). */
@OptIn(ExperimentalCoroutinesApi::class)
class SensorViewModel(application: Application) : AccountDataViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).sensorReadingDAO()

    val recentReadings: StateFlow<List<SensorReading>> = userId
        .flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList()) else dao.observeRecent(uid, 30)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}