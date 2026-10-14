
package com.fit5046.health304.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit5046.health304.model.WgerExercise
import com.fit5046.health304.repository.ExerciseRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

// Represents the possible states of the Exercise Library.
sealed interface ExerciseUiState {
    data object Loading : ExerciseUiState

    data class Success(
        val exercises: List<WgerExercise>
    ) : ExerciseUiState

    data object Empty : ExerciseUiState

    data class Error(
        val message: String
    ) : ExerciseUiState
}

class ExerciseViewModel(
    private val repository: ExerciseRepository = ExerciseRepository()
) : ViewModel() {

    var uiState by mutableStateOf<ExerciseUiState>(
        ExerciseUiState.Loading
    )
        private set

    init {
        loadExercises()
    }

    fun loadExercises() {
        viewModelScope.launch {
            uiState = ExerciseUiState.Loading

            try {
                val response = repository.getExercises()

                uiState = if (response.results.isEmpty()) {
                    ExerciseUiState.Empty
                } else {
                    ExerciseUiState.Success(response.results)
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                uiState = ExerciseUiState.Error(
                    e.localizedMessage
                        ?: "Failed to load exercises."
                )
            }
        }
    }
}
