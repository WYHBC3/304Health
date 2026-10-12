
package com.fit5046.health304.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Base URL for the public wger Exercise API
    private const val BASE_URL = "https://wger.de/api/v2/"

    // Create the API service using Retrofit
    val apiService: ExerciseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExerciseApiService::class.java)
    }
}

