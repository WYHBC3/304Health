package com.fit5046.health304.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit5046.health304.ui.theme._304HealthTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MoodDiaryEntry(
    val dateMillis: Long,
    val mood: String,
    val factors: List<String>,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodDiaryScreen(
    moodEntries: SnapshotStateList<MoodDiaryEntry>,
    onHealthTrendsClick: () -> Unit = {}
) {

    val moodOptions = listOf(
        "Very Good",
        "Good",
        "Neutral",
        "Stressed",
        "Low"
    )

    val factorOptions = listOf(
        "Stress",
        "Sleep",
        "Study / Work",
        "Exercise",
        "Health"
    )

    var selectedDateMillis by remember {
        mutableLongStateOf(
            System.currentTimeMillis()
        )
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var selectedMood by remember {
        mutableStateOf("Good")
    }

    var moodExpanded by remember {
        mutableStateOf(false)
    }

    var notes by remember {
        mutableStateOf("")
    }

    val selectedFactors = remember {
        mutableStateListOf<String>()
    }

    var editingIndex by remember {
        mutableStateOf<Int?>(null)
    }

    var viewingEntry by remember {
        mutableStateOf<MoodDiaryEntry?>(null)
    }

    val formattedDate =
        formatDate(selectedDateMillis)

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

            Text(
                text = "Mood Diary",
                style =
                    MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Record how you are feeling today",
                style =
                    MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Text(
                        text = "Health Trends",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "See how your mood changes over time and review your health trends.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Button(
                        onClick = onHealthTrendsClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = "View Health Trends",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        item {

            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                readOnly = true,

                label = {
                    Text("Date")
                },

                trailingIcon = {

                    TextButton(
                        onClick = {
                            showDatePicker = true
                        }
                    ) {
                        Text("Select")
                    }
                },

                modifier = Modifier.fillMaxWidth()
            )
        }

        item {

            ExposedDropdownMenuBox(
                expanded = moodExpanded,

                onExpandedChange = {
                    moodExpanded = it
                }
            ) {

                OutlinedTextField(
                    value = selectedMood,
                    onValueChange = {},
                    readOnly = true,

                    label = {
                        Text("Mood")
                    },

                    trailingIcon = {

                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded = moodExpanded
                            )
                    },

                    modifier = Modifier
                        .menuAnchor(
                            ExposedDropdownMenuAnchorType
                                .PrimaryNotEditable
                        )
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = moodExpanded,

                    onDismissRequest = {
                        moodExpanded = false
                    }
                ) {

                    moodOptions.forEach { mood ->

                        DropdownMenuItem(

                            text = {
                                Text(mood)
                            },

                            onClick = {
                                selectedMood = mood
                                moodExpanded = false
                            }
                        )
                    }
                }
            }
        }

        item {

            Text(
                text = "Factors affecting your mood",
                style =
                    MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(8.dp)
                ) {

                    factorOptions.forEach { factor ->

                        val checked =
                            factor in selectedFactors

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = checked,

                                onCheckedChange = {
                                        isChecked ->

                                    if (isChecked) {

                                        if (
                                            factor !in
                                            selectedFactors
                                        ) {
                                            selectedFactors.add(
                                                factor
                                            )
                                        }

                                    } else {

                                        selectedFactors.remove(
                                            factor
                                        )
                                    }
                                }
                            )

                            Text(
                                text = factor,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )
                        }
                    }
                }
            }
        }

        item {

            OutlinedTextField(
                value = notes,

                onValueChange = {
                    notes = it
                },

                label = {
                    Text("Notes")
                },

                placeholder = {
                    Text("How was your day?")
                },

                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {

            Button(
                onClick = {

                    val newEntry =
                        MoodDiaryEntry(
                            dateMillis =
                                selectedDateMillis,

                            mood =
                                selectedMood,

                            factors =
                                selectedFactors.toList(),

                            notes =
                                notes
                        )

                    if (editingIndex == null) {

                        moodEntries.add(
                            index = 0,
                            element = newEntry
                        )

                    } else {

                        moodEntries[
                            editingIndex!!
                        ] = newEntry
                    }

                    editingIndex = null

                    selectedDateMillis =
                        System.currentTimeMillis()

                    selectedMood =
                        "Good"

                    selectedFactors.clear()

                    notes = ""
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    if (editingIndex == null) {
                        "Add Mood Record"
                    } else {
                        "Update Mood Record"
                    }
                )
            }
        }

        item {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Mood History",
                style =
                    MaterialTheme.typography.titleLarge
            )
        }

        itemsIndexed(
            moodEntries
        ) { index, entry ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {

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

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text = entry.mood,

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    if (
                        entry.factors.isNotEmpty()
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Factors: ${
                                    entry.factors
                                        .joinToString(", ")
                                }",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.End
                    ) {

                        TextButton(
                            onClick = {
                                viewingEntry = entry
                            }
                        ) {
                            Text("View")
                        }

                        TextButton(
                            onClick = {

                                editingIndex =
                                    index

                                selectedDateMillis =
                                    entry.dateMillis

                                selectedMood =
                                    entry.mood

                                selectedFactors.clear()

                                selectedFactors.addAll(
                                    entry.factors
                                )

                                notes =
                                    entry.notes
                            }
                        ) {
                            Text("Edit")
                        }

                        TextButton(
                            onClick = {

                                moodEntries
                                    .removeAt(index)

                                if (
                                    editingIndex ==
                                    index
                                ) {

                                    editingIndex =
                                        null

                                    selectedFactors.clear()

                                    notes = ""
                                }
                            }
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }

        item {

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }

    if (showDatePicker) {

        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis =
                    selectedDateMillis
            )

        DatePickerDialog(

            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        datePickerState
                            .selectedDateMillis
                            ?.let {

                                selectedDateMillis =
                                    it
                            }

                        showDatePicker =
                            false
                    }
                ) {
                    Text("Confirm")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }

    viewingEntry?.let { entry ->

        AlertDialog(

            onDismissRequest = {
                viewingEntry = null
            },

            title = {
                Text("Mood Record")
            },

            text = {

                Column {

                    Text(
                        text =
                            "Date: ${
                                formatDate(
                                    entry.dateMillis
                                )
                            }"
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Mood: ${entry.mood}"
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            if (
                                entry.factors.isEmpty()
                            ) {

                                "Factors: None"

                            } else {

                                "Factors: ${
                                    entry.factors
                                        .joinToString(", ")
                                }"
                            }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            if (
                                entry.notes.isBlank()
                            ) {

                                "Notes: None"

                            } else {

                                "Notes: ${entry.notes}"
                            }
                    )
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        viewingEntry = null
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }
}

fun formatDate(
    dateMillis: Long
): String {

    return SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    ).format(
        Date(dateMillis)
    )
}

fun createDateMillis(
    year: Int,
    month: Int,
    day: Int
): Long {

    return Calendar
        .getInstance()
        .apply {

            set(
                Calendar.YEAR,
                year
            )

            set(
                Calendar.MONTH,
                month - 1
            )

            set(
                Calendar.DAY_OF_MONTH,
                day
            )

            set(
                Calendar.HOUR_OF_DAY,
                12
            )

            set(
                Calendar.MINUTE,
                0
            )

            set(
                Calendar.SECOND,
                0
            )

            set(
                Calendar.MILLISECOND,
                0
            )

        }.timeInMillis
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun MoodDiaryScreenPreview() {

    val previewEntries =
        remember {
            mutableStateListOf(
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
                        "Feeling a little tired."
                )
            )
        }

    _304HealthTheme {

        MoodDiaryScreen(
            moodEntries =
                previewEntries
        )
    }
}