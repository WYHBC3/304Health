package com.fit5046.health304.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit5046.health304.ui.theme._304HealthTheme

@Composable
fun HomeDashboardScreen(
    moodEntries: List<MoodDiaryEntry> = emptyList(),
    onMoodClick: () -> Unit = {},
    onViewAllMoodClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onHealthTrendsClick: () -> Unit = {}
) {

    var moodCompleted by remember {
        mutableStateOf(true)
    }

    var medicationCompleted by remember {
        mutableStateOf(false)
    }

    var exerciseCompleted by remember {
        mutableStateOf(true)
    }

    val completedTasks =
        listOf(
            moodCompleted,
            medicationCompleted,
            exerciseCompleted
        ).count {
            it
        }

    val progress =
        completedTasks / 3f

    val latestMoodEntry =
        moodEntries
            .maxByOrNull {
                it.dateMillis
            }

    val recentMoodEntries =
        moodEntries
            .sortedByDescending {
                it.dateMillis
            }
            .take(3)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 24.dp,
                vertical = 24.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        item {

            Box(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "304Health",

                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,

                    modifier =
                        Modifier.align(
                            Alignment.Center
                        ),

                    textAlign =
                        TextAlign.Center
                )

                Row(
                    modifier =
                        Modifier.align(
                            Alignment.CenterEnd
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick =
                            onSearchClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Filled.Search,

                            contentDescription =
                                "Search",

                            modifier =
                                Modifier.size(28.dp),

                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )
                    }

                    IconButton(
                        onClick =
                            onProfileClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Filled
                                    .AccountCircle,

                            contentDescription =
                                "Profile",

                            modifier =
                                Modifier.size(36.dp),

                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "Your wellness at a glance",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,

                modifier =
                    Modifier.fillMaxWidth(),

                textAlign =
                    TextAlign.Center
            )
        }

        item {

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = "Welcome back!",

                style =
                    MaterialTheme
                        .typography
                        .titleLarge
            )

            Text(
                text =
                    "Take care of yourself today.",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }

        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {

                    Text(
                        text =
                            "Today's Progress",

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    LinearProgressIndicator(
                        progress = {
                            progress
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "$completedTasks of 3 daily tasks completed",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }
            }
        }

        /*
         * Explicit Search entry.
         */
        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                    horizontalArrangement =
                        Arrangement
                            .SpaceBetween,

                    verticalAlignment =
                        Alignment
                            .CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "Search Health Content",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Search mood records and access health features.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }

                    TextButton(
                        onClick =
                            onSearchClick
                    ) {

                        Text("Search")
                    }
                }
            }
        }

        /*
         * Explicit P4 Data Visualization entry.
         */
        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                    horizontalArrangement =
                        Arrangement
                            .SpaceBetween,

                    verticalAlignment =
                        Alignment
                            .CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "Health Trends",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "View your mood and health trends over time.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }

                    TextButton(
                        onClick =
                            onHealthTrendsClick
                    ) {

                        Text("View")
                    }
                }
            }
        }

        item {

            Text(
                text = "Latest Mood",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                    verticalAlignment =
                        Alignment
                            .CenterVertically,

                    horizontalArrangement =
                        Arrangement
                            .SpaceBetween
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                latestMoodEntry
                                    ?.mood
                                    ?: "No record",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium
                        )

                        Text(
                            text =
                                if (
                                    latestMoodEntry != null
                                ) {

                                    formatDate(
                                        latestMoodEntry
                                            .dateMillis
                                    )

                                } else {

                                    "Add your first mood record."
                                },

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }

                    Button(
                        onClick =
                            onMoodClick
                    ) {

                        Text(
                            if (
                                latestMoodEntry == null
                            ) {
                                "Add Mood"
                            } else {
                                "Update Mood"
                            }
                        )
                    }
                }
            }
        }

        item {

            Text(
                text =
                    "Today's Checklist",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(8.dp)
                ) {

                    ChecklistItem(
                        text =
                            "Mood diary completed",

                        checked =
                            moodCompleted,

                        onCheckedChange = {
                            moodCompleted = it
                        }
                    )

                    ChecklistItem(
                        text =
                            "Take medication",

                        checked =
                            medicationCompleted,

                        onCheckedChange = {
                            medicationCompleted =
                                it
                        }
                    )

                    ChecklistItem(
                        text =
                            "Home exercise completed",

                        checked =
                            exerciseCompleted,

                        onCheckedChange = {
                            exerciseCompleted =
                                it
                        }
                    )
                }
            }
        }

        item {

            Text(
                text =
                    "Recent Mood Records",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )
        }

        if (
            recentMoodEntries.isEmpty()
        ) {

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(
                        text =
                            "No mood records yet.",

                        modifier =
                            Modifier.padding(16.dp),

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )
                }
            }

        } else {

            items(
                recentMoodEntries
            ) { entry ->

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),

                        horizontalArrangement =
                            Arrangement
                                .SpaceBetween,

                        verticalAlignment =
                            Alignment
                                .CenterVertically
                    ) {

                        Column {

                            Text(
                                text =
                                    formatDate(
                                        entry.dateMillis
                                    ),

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )

                            Text(
                                text =
                                    entry.mood,

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium
                            )
                        }

                        TextButton(
                            onClick =
                                onViewAllMoodClick
                        ) {

                            Text("View")
                        }
                    }
                }
            }
        }

        item {

            TextButton(
                onClick =
                    onViewAllMoodClick,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    "View All Mood Records"
                )
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )
        }
    }
}

@Composable
private fun ChecklistItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Checkbox(
            checked = checked,
            onCheckedChange =
                onCheckedChange
        )

        Text(
            text = text,

            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun HomeDashboardScreenPreview() {

    val previewEntries =
        listOf(
            MoodDiaryEntry(
                dateMillis =
                    createDateMillis(
                        2026,
                        9,
                        8
                    ),

                mood = "Good",

                factors =
                    listOf("Exercise"),

                notes =
                    "Had a productive day."
            ),

            MoodDiaryEntry(
                dateMillis =
                    createDateMillis(
                        2026,
                        9,
                        7
                    ),

                mood = "Neutral",

                factors =
                    listOf("Sleep"),

                notes =
                    "Feeling tired."
            )
        )

    _304HealthTheme {

        HomeDashboardScreen(
            moodEntries =
                previewEntries
        )
    }
}