package com.fit5046.health304.ui.screens

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit5046.health304.ui.theme._304HealthTheme
import com.fit5046.health304.data.MedicationReminder

data class SearchItem(
    val title: String,
    val type: String,
    val description: String
)

@Composable
fun SearchScreen(
    moodEntries: List<MoodDiaryEntry> = emptyList(),
    medicationReminders: List<MedicationReminder> = emptyList(),
    onBackClick: () -> Unit = {},
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

    /*
     * Real search results generated from
     * the shared Mood Diary records.
     */
    val moodSearchItems =
        moodEntries
            .sortedByDescending {
                it.dateMillis
            }
            .map { entry ->

                SearchItem(
                    title =
                        "${
                            formatDate(
                                entry.dateMillis
                            )
                        } - ${entry.mood}",

                    type = "Mood",

                    description =
                        buildString {

                            if (
                                entry.factors
                                    .isNotEmpty()
                            ) {

                                append(
                                    "Factors: ${
                                        entry.factors
                                            .joinToString(
                                                ", "
                                            )
                                    }"
                                )
                            }

                            if (
                                entry.notes
                                    .isNotBlank()
                            ) {

                                if (
                                    isNotEmpty()
                                ) {
                                    append(" · ")
                                }

                                append(
                                    entry.notes
                                )
                            }

                            if (
                                isEmpty()
                            ) {
                                append(
                                    "Mood diary record"
                                )
                            }
                        }
                )
            }

    /*
     * Other modules do not yet expose
     * shared record data, so these act
     * as searchable feature entries.
     */

    // Convert actual Room medication records
    // into searchable items.
    val medicationSearchItems = medicationReminders.map { reminder ->

        val time = "%02d:%02d".format(
            reminder.hour,
            reminder.minute
        )

        SearchItem(
            title = reminder.medicationName,
            type = "Medication",
            description = "Reminder: $time · " +
                    if (reminder.reminderEnabled) {
                        "Enabled"
                    } else {
                        "Disabled"
                    }
        )
    }

    val appItems =
        listOf(

            SearchItem(
                title =
                    "Medication Reminder",

                type =
                    "Medication",

                description =
                    "View and manage your medication reminders."
            ),

            SearchItem(
                title =
                    "Exercise Library",

                type =
                    "Exercise",

                description =
                    "Browse exercises and health activities."
            ),

            SearchItem(
                title =
                    "Health Trends",

                type =
                    "Health Data",

                description =
                    "View your mood and health trends over time."
            )
        )

    val searchItems =
        moodSearchItems +
                medicationSearchItems +
                appItems

    val filteredItems =
        searchItems.filter { item ->

            val matchesSearch =
                searchText.isBlank() ||
                        item.title.contains(
                            searchText,
                            ignoreCase = true
                        ) ||
                        item.type.contains(
                            searchText,
                            ignoreCase = true
                        ) ||
                        item.description.contains(
                            searchText,
                            ignoreCase = true
                        )

            val matchesType =
                selectedType == "All" ||
                        item.type ==
                        selectedType

            matchesSearch &&
                    matchesType
        }

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        item {

            TextButton(
                onClick =
                    onBackClick
            ) {

                Text("← Back")
            }

            Text(
                text = "Search",

                style =
                    MaterialTheme
                        .typography
                        .headlineMedium
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "Search across your health information",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }

        item {

            OutlinedTextField(
                value =
                    searchText,

                onValueChange = {
                    searchText = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                placeholder = {
                    Text(
                        "Search health content..."
                    )
                },

                singleLine = true,

                shape =
                    RoundedCornerShape(
                        16.dp
                    )
            )
        }

        item {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    )
            ) {

                listOf(
                    "All",
                    "Mood",
                    "Medication"
                ).forEach { type ->

                    FilterChip(
                        selected =
                            selectedType ==
                                    type,

                        onClick = {
                            selectedType =
                                type
                        },

                        label = {
                            Text(type)
                        }
                    )
                }
            }

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    )
            ) {

                FilterChip(
                    selected =
                        selectedType ==
                                "Exercise",

                    onClick = {
                        selectedType =
                            "Exercise"
                    },

                    label = {
                        Text(
                            "Exercise"
                        )
                    }
                )

                FilterChip(
                    selected =
                        selectedType ==
                                "Health Data",

                    onClick = {
                        selectedType =
                            "Health Data"
                    },

                    label = {
                        Text(
                            "Health Data"
                        )
                    }
                )
            }
        }

        item {

            Text(
                text =
                    "Search Results",

                fontSize = 18.sp,

                fontWeight =
                    FontWeight.SemiBold
            )
        }

        if (
            filteredItems.isEmpty()
        ) {

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(
                        text =
                            "No matching results found.",

                        modifier =
                            Modifier.padding(
                                18.dp
                            ),

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )
                }
            }

        } else {

            items(
                filteredItems
            ) { item ->

                SearchResultCard(
                    item = item,

                    onClick = {

                        when (
                            item.type
                        ) {

                            "Mood" ->
                                onMoodClick()

                            "Medication" ->
                                onMedicationClick()

                            "Exercise" ->
                                onExerciseClick()

                            "Health Data" ->
                                onHealthDataClick()
                        }
                    }
                )
            }
        }

        item {

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }
    }
}

@Composable
private fun SearchResultCard(
    item: SearchItem,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },

        shape =
            RoundedCornerShape(
                18.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Text(
                text =
                    item.title,

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    item.type,

                style =
                    MaterialTheme
                        .typography
                        .labelMedium,

                color =
                    MaterialTheme
                        .colorScheme
                        .primary
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    item.description,

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
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

    val previewEntries =
        listOf(
            MoodDiaryEntry(
                dateMillis =
                    createDateMillis(
                        2026,
                        9,
                        11
                    ),

                mood =
                    "Stressed",

                factors =
                    listOf(
                        "Study / Work",
                        "Sleep"
                    ),

                notes =
                    "Assignment deadline tomorrow."
            ),

            MoodDiaryEntry(
                dateMillis =
                    createDateMillis(
                        2026,
                        9,
                        10
                    ),

                mood =
                    "Very Good",

                factors =
                    listOf("Exercise"),

                notes =
                    "Had a great workout."
            )
        )

    _304HealthTheme {

        SearchScreen(
            moodEntries =
                previewEntries
        )
    }
}