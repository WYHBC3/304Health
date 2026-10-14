package com.fit5046.health304.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.fit5046.health304.data.Recommendation
import com.fit5046.health304.repository.ContextRepository
import com.fit5046.health304.worker.WorkScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class RecommendationViewModel(application: Application) : AccountDataViewModel(application) {

    private val repository = ContextRepository(application)

    // Re-subscribes automatically when the signed-in account changes
    val recommendation: StateFlow<Recommendation?> = userId
        .flatMapLatest { uid ->
            if (uid == null) flowOf(null) else repository.observeRecommendation(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun refresh() {
        WorkScheduler.runNow(getApplication())
    }
}