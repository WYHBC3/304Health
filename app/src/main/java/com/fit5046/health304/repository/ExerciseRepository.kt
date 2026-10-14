
package com.fit5046.health304.repository

import com.fit5046.health304.model.ExerciseResponse
import com.fit5046.health304.network.RetrofitClient

class ExerciseRepository {

    // Fetch exercise data from the public wger API
    suspend fun getExercises(): ExerciseResponse {
        return RetrofitClient.apiService.getExercises(
            limit = 30
        )
    }
}
