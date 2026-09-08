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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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

data class MoodRecord(
    val date: String,
    val mood: String
)

@Composable
fun HomeDashboardScreen(
    onMoodClick: () -> Unit = {},
    onViewAllMoodClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var moodCompleted by remember { mutableStateOf(true) }
    var medicationCompleted by remember { mutableStateOf(false) }
    var exerciseCompleted by remember { mutableStateOf(true) }

    val completedTasks = listOf(
        moodCompleted,
        medicationCompleted,
        exerciseCompleted
    ).count { it }

    val progress = completedTasks / 3f

    val moodRecords = listOf(
        MoodRecord("8 Sep", "Good"),
        MoodRecord("7 Sep", "Neutral"),
        MoodRecord("6 Sep", "Stressed")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )

                TextButton(
                    onClick = onProfileClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text("Profile")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your wellness at a glance",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Take care of yourself today.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Today's Progress",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$completedTasks of 3 daily tasks completed",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            Text(
                text = "Today's Mood",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Good",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "You are feeling good today.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = onMoodClick
                    ) {
                        Text("Update Mood")
                    }
                }
            }
        }

        item {
            Text(
                text = "Today's Checklist",
                style = MaterialTheme.typography.titleMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    ChecklistItem(
                        text = "Mood diary completed",
                        checked = moodCompleted,
                        onCheckedChange = { moodCompleted = it }
                    )

                    ChecklistItem(
                        text = "Take medication",
                        checked = medicationCompleted,
                        onCheckedChange = { medicationCompleted = it }
                    )

                    ChecklistItem(
                        text = "Home exercise completed",
                        checked = exerciseCompleted,
                        onCheckedChange = { exerciseCompleted = it }
                    )
                }
            }
        }

        item {
            Text(
                text = "Recent Mood Records",
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(moodRecords) { record ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = record.date,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = record.mood,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    TextButton(
                        onClick = onViewAllMoodClick
                    ) {
                        Text("View")
                    }
                }
            }
        }

        item {
            TextButton(
                onClick = onViewAllMoodClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View All Mood Records")
            }

            Spacer(modifier = Modifier.height(16.dp))
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
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeDashboardScreenPreview() {
    _304HealthTheme {
        HomeDashboardScreen()
    }
}