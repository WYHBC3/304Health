
package com.fit5046.health304.ui.screens

import android.text.Html
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit5046.health304.model.WgerExercise
import com.fit5046.health304.model.ExerciseTranslation
import com.fit5046.health304.viewmodel.ExerciseUiState
import com.fit5046.health304.viewmodel.ExerciseViewModel

// Keep the existing callback type for compatibility.
data class Exercise(
    val name: String,
    val category: String,
    val difficulty: String = "",
    val duration: String = ""
)

// Prefer the English translation (language ID 2).
private fun getTranslation(
    exercise: WgerExercise
): ExerciseTranslation? {
    return exercise.translations.firstOrNull {
        it.language == 2 && it.name.isNotBlank()
    } ?: exercise.translations.firstOrNull {
        it.name.isNotBlank()
    }
}

private fun getExerciseName(exercise: WgerExercise): String {
    return getTranslation(exercise)?.name
        ?.takeIf { it.isNotBlank() }
        ?: "Exercise #${exercise.id}"
}

private fun getCategory(exercise: WgerExercise): String {
    return exercise.category?.name ?: "Other"
}

@Composable
fun ExerciseLibraryScreen(
    onExerciseClick: (Exercise) -> Unit = {}
) {
    val exerciseViewModel: ExerciseViewModel = viewModel()

    val uiState = exerciseViewModel.uiState

    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    var selectedCategory by rememberSaveable {
        mutableStateOf("All")
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    // Data comes from the ViewModel, not a static list.
    val exercises = when (uiState) {
        is ExerciseUiState.Success -> uiState.exercises
        else -> emptyList()
    }

    val categories = listOf("All") +
            exercises.map { getCategory(it) }
                .distinct()
                .sorted()

    val filteredExercises = exercises.filter { exercise ->
        val matchesSearch = getExerciseName(exercise)
            .contains(searchText, ignoreCase = true)

        val matchesCategory =
            selectedCategory == "All" ||
                    getCategory(exercise) == selectedCategory

        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Exercise Library",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Explore exercises from the wger API",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search exercises") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        @OptIn(ExperimentalMaterial3Api::class)
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = categoryExpanded
                    )
                },
                modifier = Modifier
                    .menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable
                    )
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = {
                    categoryExpanded = false
                }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (val state = uiState) {

            is ExerciseUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
                Text("Loading exercises...")
            }

            is ExerciseUiState.Error -> {
                Text(
                    text = "Failed to load: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        exerciseViewModel.loadExercises()
                    }
                ) {
                    Text("Retry")
                }
            }

            is ExerciseUiState.Empty -> {
                Text("No exercises available from the API.")
            }

            is ExerciseUiState.Success -> {

                Text(
                    text = "Available Exercises (${filteredExercises.size})",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredExercises.isEmpty()) {
                    Text("No exercises match your search.")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(
                            items = filteredExercises,
                            key = { it.id }
                        ) { exercise ->
                            ExerciseCard(
                                exercise = exercise,
                                onExerciseClick = onExerciseClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: WgerExercise,
    onExerciseClick: (Exercise) -> Unit
) {
    val name = getExerciseName(exercise)
    val category = getCategory(exercise)
    val translation = getTranslation(exercise)

    var showDetails by rememberSaveable(exercise.id) {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Category: $category")

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    showDetails = !showDetails

                    if (showDetails) {
                        onExerciseClick(
                            Exercise(
                                name = name,
                                category = category
                            )
                        )
                    }
                }
            ) {
                Text(
                    if (showDetails) "Hide Details"
                    else "View Exercise"
                )
            }

            if (showDetails) {
                Spacer(modifier = Modifier.height(12.dp))

                val rawDescription =
                    translation?.description.orEmpty()

                val description = Html.fromHtml(
                    rawDescription,
                    Html.FROM_HTML_MODE_COMPACT
                ).toString().trim()

                Text(
                    text = description.ifBlank {
                        "No description available."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
