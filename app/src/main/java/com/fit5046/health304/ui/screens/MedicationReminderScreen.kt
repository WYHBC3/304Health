package com.fit5046.health304.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
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
import java.util.Locale

/**
 * Static UI for creating a medication reminder.
 *
 * The prototype includes the required TimePicker and Switch. AlarmManager will
 * be connected in Assessment 4, so saving currently only returns screen values.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationReminderScreen(
    onSave: (medicationName: String, hour: Int, minute: Int, reminderEnabled: Boolean) -> Unit =
        { _, _, _, _ -> }
) {
    var medicationName by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = 8,
        initialMinute = 0,
        is24Hour = false
    )
    val selectedTime = String.format(
        Locale.getDefault(),
        "%02d:%02d",
        timePickerState.hour,
        timePickerState.minute
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Medication Reminder",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Set a reminder to take your medication on time",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = medicationName,
            onValueChange = { medicationName = it },
            label = { Text("Medication name *") },
            placeholder = { Text("For example, Vitamin D") },
            supportingText = { Text("Enter the name shown on your medication label.") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = selectedTime,
            onValueChange = {},
            readOnly = true,
            label = { Text("Reminder time *") },
            trailingIcon = {
                TextButton(onClick = { showTimePicker = true }) {
                    Text("Select")
                }
            },
            supportingText = { Text("Choose when you would like to receive the reminder.") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Enable reminder",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "You can change this setting at any time.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                checked = reminderEnabled,
                onCheckedChange = { reminderEnabled = it }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Fields marked with * are required.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onSave(
                    medicationName,
                    timePickerState.hour,
                    timePickerState.minute,
                    reminderEnabled
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Reminder")
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select reminder time") },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MedicationReminderScreenPreview() {
    _304HealthTheme {
        MedicationReminderScreen()
    }
}
