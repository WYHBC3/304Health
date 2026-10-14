
package com.fit5046.health304.model

// Response returned by the wger Exercise API
data class ExerciseResponse(
    val count: Int,
    val results: List<WgerExercise>
)

// A single exercise
data class WgerExercise(
    val id: Int,
    val category: ExerciseCategory?,
    val translations: List<ExerciseTranslation>
)

// Exercise category
data class ExerciseCategory(
    val id: Int,
    val name: String
)

// Exercise name and description in different languages
data class ExerciseTranslation(
    val language: Int,
    val name: String,
    val description: String?
)
