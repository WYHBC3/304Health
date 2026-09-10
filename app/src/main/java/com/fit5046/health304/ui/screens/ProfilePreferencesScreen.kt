package com.fit5046.health304.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit5046.health304.ui.theme._304HealthTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Static UI for the user's health preferences.
 *
 * This A2 prototype demonstrates the required DatePicker and Expanded Dropdown
 * Menu components. Saving only returns the values to a future navigation/data
 * layer; it does not persist data yet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePreferencesScreen(
    onSave: (dateOfBirth: String, healthGoal: String, activityLevel: String) -> Unit = { _, _, _ -> },
    onLogout: () -> Unit = {}
) {
    val healthGoals = listOf(
        "Improve fitness",
        "Manage stress",
        "Build healthy habits",
        "Improve sleep"
    )
    val activityLevels = listOf("Low", "Moderate", "High")

    var selectedDateMillis by remember { mutableLongStateOf(0L) }
    var showDatePicker by remember { mutableStateOf(false) }
    var healthGoalExpanded by remember { mutableStateOf(false) }
    var activityLevelExpanded by remember { mutableStateOf(false) }
    var selectedHealthGoal by remember { mutableStateOf("") }
    var selectedActivityLevel by remember { mutableStateOf("") }

    val selectedDate = if (selectedDateMillis == 0L) {
        ""
    } else {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(selectedDateMillis))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Health Preferences",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tell us about your goals so we can personalise your wellness journey",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            readOnly = true,
            label = { Text("Date of birth *") },
            placeholder = { Text("Select your date of birth") },
            trailingIcon = {
                TextButton(onClick = { showDatePicker = true }) {
                    Text("Select")
                }
            },
            supportingText = { Text("This helps us tailor suitable activity suggestions.") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = healthGoalExpanded,
            onExpandedChange = { healthGoalExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedHealthGoal,
                onValueChange = {},
                readOnly = true,
                label = { Text("Primary health goal *") },
                placeholder = { Text("Select a goal") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = healthGoalExpanded)
                },
                supportingText = { Text("Choose the goal you want to focus on first.") },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = healthGoalExpanded,
                onDismissRequest = { healthGoalExpanded = false }
            ) {
                healthGoals.forEach { goal ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(goal) },
                        onClick = {
                            selectedHealthGoal = goal
                            healthGoalExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = activityLevelExpanded,
            onExpandedChange = { activityLevelExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedActivityLevel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Current activity level *") },
                placeholder = { Text("Select your activity level") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityLevelExpanded)
                },
                supportingText = { Text("Choose the option that best describes a typical week.") },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = activityLevelExpanded,
                onDismissRequest = { activityLevelExpanded = false }
            ) {
                activityLevels.forEach { level ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(level) },
                        onClick = {
                            selectedActivityLevel = level
                            activityLevelExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Fields marked with * are required.",
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onSave(selectedDate, selectedHealthGoal, selectedActivityLevel)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Preferences")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out")
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis.takeIf { it != 0L }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDateMillis = it }
                        showDatePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfilePreferencesScreenPreview() {
    _304HealthTheme {
        ProfilePreferencesScreen()
    }
}
