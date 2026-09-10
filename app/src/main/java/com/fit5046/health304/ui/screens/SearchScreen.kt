package com.fit5046.health304.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SearchItem(
    val title: String,
    val type: String,
    val description: String
)

@Composable
fun SearchScreen(
    onMoodClick: () -> Unit = {},
    onMedicationClick: () -> Unit = {},
    onExerciseClick: () -> Unit = {},
    onHealthDataClick: () -> Unit = {}
) {

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedType by remember {
        mutableStateOf("All")
    }

    val searchItems = listOf(
        SearchItem(
            title = "Mood Journal",
            type = "Mood",
            description = "View and manage your recorded mood entries."
        ),
        SearchItem(
            title = "Feeling Happy",
            type = "Mood",
            description = "A positive mood record from your recent journal."
        ),
        SearchItem(
            title = "Medication Reminder",
            type = "Medication",
            description = "View and manage your medication reminder settings."
        ),
        SearchItem(
            title = "Morning Medication",
            type = "Medication",
            description = "Medication reminder scheduled for the morning."
        ),
        SearchItem(
            title = "Walking",
            type = "Exercise",
            description = "Low-impact exercise suitable for most fitness levels."
        ),
        SearchItem(
            title = "Stretching",
            type = "Exercise",
            description = "Gentle activity that supports flexibility and recovery."
        ),
        SearchItem(
            title = "Heart Rate Trend",
            type = "Health Data",
            description = "Review your weekly heart rate information."
        ),
        SearchItem(
            title = "Weekly Health Overview",
            type = "Health Data",
            description = "View a summary of your recent health trends."
        )
    )

    val filteredItems = searchItems.filter { item ->

        val matchesSearch =
            item.title.contains(searchText, ignoreCase = true) ||
                    item.type.contains(searchText, ignoreCase = true) ||
                    item.description.contains(searchText, ignoreCase = true)

        val matchesType =
            selectedType == "All" ||
                    item.type == selectedType

        matchesSearch && matchesType
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .padding(20.dp)
    ) {

        Text(
            text = "Search",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Search across your health information",
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
                Text("Search health content...")
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // First row of filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                "All",
                "Mood",
                "Medication"
            ).forEach { type ->

                FilterChip(
                    selected = selectedType == type,
                    onClick = {
                        selectedType = type
                    },
                    label = {
                        Text(type)
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        // Second row of filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = selectedType == "Exercise",
                onClick = {
                    selectedType = "Exercise"
                },
                label = {
                    Text("Exercise")
                }
            )

            FilterChip(
                selected = selectedType == "Health Data",
                onClick = {
                    selectedType = "Health Data"
                },
                label = {
                    Text("Health Data")
                }
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Search Results",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (filteredItems.isEmpty()) {

            Text(
                text = "No matching results found.",
                fontSize = 14.sp,
                color = Color.Gray
            )

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(filteredItems) { item ->

                    SearchResultCard(
                        item = item,
                        onClick = {
                            when (item.type) {

                                "Mood" -> {
                                    onMoodClick()
                                }

                                "Medication" -> {
                                    onMedicationClick()
                                }

                                "Exercise" -> {
                                    onExerciseClick()
                                }

                                "Health Data" -> {
                                    onHealthDataClick()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    item: SearchItem,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = item.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = item.type,
                fontSize = 13.sp,
                color = Color(0xFF6750A4)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = item.description,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SearchScreenPreview() {
    SearchScreen()
}