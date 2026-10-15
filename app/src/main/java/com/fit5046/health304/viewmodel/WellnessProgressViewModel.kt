package com.fit5046.health304.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit5046.health304.data.WellnessProgress
import com.fit5046.health304.repository.WellnessProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WellnessProgressViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        WellnessProgressRepository(application)

    fun getProgress(
        userId: String,
        dateKey: String
    ): Flow<WellnessProgress?> {

        return repository.getProgress(
            userId,
            dateKey
        )
    }

    fun saveProgress(
        progress: WellnessProgress
    ) =
        viewModelScope.launch(
            Dispatchers.IO
        ) {

            repository.saveProgress(
                progress
            )
        }
}