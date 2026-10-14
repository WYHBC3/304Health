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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit5046.health304.data.MoodEntry
import com.fit5046.health304.viewmodel.MoodViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodDiaryScreen(
    onHealthTrendsClick: () -> Unit = {},
    moodViewModel: MoodViewModel = viewModel()
) {

    // Get the currently signed-in Firebase user's UID.
    // Each user can only load their own mood records.
    val userId =
        FirebaseAuth.getInstance().currentUser?.uid ?: return

    // Room Flow automatically updates this list whenever the database changes.
    val moodEntries by
    moodViewModel
        .getMoodEntries(userId)
        .collectAsState(initial = emptyList())

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

    // Stores the Room record currently being edited.
    var editingEntry by remember {
        mutableStateOf<MoodEntry?>(null)
    }

    // Stores the Room record currently being viewed.
    var viewingEntry by remember {
        mutableStateOf<MoodEntry?>(null)
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

        // Page title
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


        // Health Trends card
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
                        style =
                            MaterialTheme.typography.titleLarge
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "See how your mood changes over time and review your health trends.",
                        style =
                            MaterialTheme.typography.bodyMedium
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
                            style =
                                MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }


        // Date
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


        // Mood dropdown
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


        // Mood factors
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
                                            factor !in selectedFactors
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


        // Notes
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


        // Add / Update button
        item {

            Button(
                onClick = {

                    val currentEditingEntry =
                        editingEntry

                    if (currentEditingEntry == null) {

                        // Create a new Room record.
                        val newEntry =
                            MoodEntry(
                                userId = userId,
                                dateMillis =
                                    selectedDateMillis,
                                mood =
                                    selectedMood,

                                // Store selected factors as one String.
                                factors =
                                    selectedFactors
                                        .joinToString(","),

                                notes =
                                    notes
                            )

                        moodViewModel
                            .insertMoodEntry(newEntry)

                    } else {

                        // Keep the existing Room ID when updating.
                        val updatedEntry =
                            currentEditingEntry.copy(
                                dateMillis =
                                    selectedDateMillis,
                                mood =
                                    selectedMood,
                                factors =
                                    selectedFactors
                                        .joinToString(","),
                                notes =
                                    notes
                            )

                        moodViewModel
                            .updateMoodEntry(updatedEntry)
                    }

                    // Clear edit mode and reset the form.
                    editingEntry = null

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
                    if (editingEntry == null) {
                        "Add Mood Record"
                    } else {
                        "Update Mood Record"
                    }
                )
            }
        }


        // Mood History heading
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


        // Empty state
        if (moodEntries.isEmpty()) {

            item {

                Text(
                    text = "No mood records yet.",
                    style =
                        MaterialTheme.typography.bodyMedium,
                    modifier =
                        Modifier.fillMaxWidth(),
                    textAlign =
                        TextAlign.Center
                )
            }

        } else {

            // Room data
            items(
                items = moodEntries,
                key = { entry ->
                    entry.id
                }
            ) { entry ->

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
                            entry.factors.isNotBlank()
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    "Factors: ${
                                        entry.factors
                                            .split(",")
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

                            // View record
                            TextButton(
                                onClick = {
                                    viewingEntry =
                                        entry
                                }
                            ) {
                                Text("View")
                            }


                            // Edit record
                            TextButton(
                                onClick = {

                                    editingEntry =
                                        entry

                                    selectedDateMillis =
                                        entry.dateMillis

                                    selectedMood =
                                        entry.mood

                                    selectedFactors.clear()

                                    if (
                                        entry.factors
                                            .isNotBlank()
                                    ) {

                                        selectedFactors.addAll(
                                            entry.factors
                                                .split(",")
                                        )
                                    }

                                    notes =
                                        entry.notes
                                }
                            ) {
                                Text("Edit")
                            }


                            // Delete record
                            TextButton(
                                onClick = {

                                    moodViewModel
                                        .deleteMoodEntry(
                                            entry
                                        )

                                    // If the deleted record
                                    // was being edited,
                                    // reset the form.
                                    if (
                                        editingEntry?.id ==
                                        entry.id
                                    ) {

                                        editingEntry =
                                            null

                                        selectedDateMillis =
                                            System
                                                .currentTimeMillis()

                                        selectedMood =
                                            "Good"

                                        selectedFactors
                                            .clear()

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
        }


        item {

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )
        }
    }


    // Date Picker
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
                        showDatePicker =
                            false
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


    // View Mood Record dialog
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
                                entry.factors
                                    .isBlank()
                            ) {

                                "Factors: None"

                            } else {

                                "Factors: ${
                                    entry.factors
                                        .split(",")
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