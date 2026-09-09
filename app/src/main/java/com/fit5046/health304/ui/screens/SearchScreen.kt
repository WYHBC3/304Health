package com.fit5046.health304.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ExerciseItem(
    val name: String,
    val category: String,
    val duration: String,
    val description: String
)

@Composable
fun SearchScreen() {

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("All")
    }

    val exercises = listOf(
        ExerciseItem(
            name = "Walking",
            category = "Cardio",
            duration = "20–30 min",
            description = "Low-impact activity suitable for most fitness levels."
        ),
        ExerciseItem(
            name = "Jogging",
            category = "Cardio",
            duration = "20 min",
            description = "Moderate cardio exercise to improve endurance."
        ),
        ExerciseItem(
            name = "Bodyweight Squat",
            category = "Strength",
            duration = "3 × 12",
            description = "Simple lower-body strength exercise requiring no equipment."
        ),
        ExerciseItem(
            name = "Stretching",
            category = "Flexibility",
            duration = "10–15 min",
            description = "Gentle stretching routine to support mobility and recovery."
        )
    )

    val filteredExercises = exercises.filter { exercise ->
        val matchesSearch =
            exercise.name.contains(searchText, ignoreCase = true)

        val matchesCategory =
            selectedCategory == "All" ||
                    exercise.category == selectedCategory

        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .padding(20.dp)
    ) {

        Text(
            text = "Exercise Library",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Find exercises and health activities",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search exercises...")
            },


            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                "All",
                "Cardio",
                "Strength",
                "Flexibility"
            ).forEach { category ->

                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        selectedCategory = category
                    },
                    label = {
                        Text(category)
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Recommended Exercises",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(filteredExercises) { exercise ->

                ExerciseCard(
                    exercise = exercise
                )
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseItem
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = exercise.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = exercise.duration,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = exercise.category,
                fontSize = 13.sp,
                color = Color(0xFF6750A4)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = exercise.description,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}