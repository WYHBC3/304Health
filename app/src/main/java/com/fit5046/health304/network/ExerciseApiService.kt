
package com.fit5046.health304.network

import com.fit5046.health304.model.ExerciseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ExerciseApiService {

    @GET("exerciseinfo/")
    suspend fun getExercises(
        @Query("limit") limit: Int = 30
    ): ExerciseResponse

}

