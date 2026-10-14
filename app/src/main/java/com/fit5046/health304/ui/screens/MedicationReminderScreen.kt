package com.fit5046.health304.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit5046.health304.ui.theme._304HealthTheme
import com.fit5046.health304.data.MedicationReminder
import java.util.Locale

/**
 * Form and saved reminder list. Room operations are handled by the route/ViewModel.
 * Saved changes are observed by the application's daily alarm scheduler.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationReminderScreen(
    onSave: (medicationName: String, hour: Int, minute: Int, reminderEnabled: Boolean) -> Unit =
        { _, _, _, _ -> },
    initialReminder: MedicationReminder? = null,
    reminders: List<MedicationReminder> = emptyList(),
    onEdit: (MedicationReminder) -> Unit = {},
    onDelete: (MedicationReminder) -> Unit = {},
    onCancel: () -> Unit = {},
    isSaving: Boolean = false,
    statusMessage: String? = null,
    permissionContent: @Composable () -> Unit = {}
) {
    var medicationName by rememberSaveable { mutableStateOf(initialReminder?.medicationName.orEmpty()) }
    var reminderEnabled by rememberSaveable { mutableStateOf(initialReminder?.reminderEnabled ?: true) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedHour by rememberSaveable { mutableIntStateOf(initialReminder?.hour ?: 8) }
    var selectedMinute by rememberSaveable { mutableIntStateOf(initialReminder?.minute ?: 0) }
    var showValidationErrors by remember { mutableStateOf(false) }
    val medicationNameError = showValidationErrors && medicationName.isBlank()
    val selectedTime = String.format(
        Locale.getDefault(),
        "%02d:%02d",
        selectedHour,
        selectedMinute
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
            text = "Save and manage your medication reminder times",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        permissionContent()

        OutlinedTextField(
            value = medicationName,
            onValueChange = { medicationName = it },
            label = { Text("Medication name *") },
            placeholder = { Text("For example, Vitamin D") },
            isError = medicationNameError,
            supportingText = {
                Text(
                    if (medicationNameError) "Please enter a medication name."
                    else "Enter the name shown on your medication label."
                )
            },
            singleLine = true,
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = selectedTime,
            onValueChange = {},
            readOnly = true,
            label = { Text("Reminder time *") },
            trailingIcon = {
                TextButton(onClick = { showTimePicker = true }, enabled = !isSaving) {
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
                enabled = !isSaving,
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
                if (medicationName.isBlank()) {
                    showValidationErrors = true
                } else {
                    onSave(medicationName, selectedHour, selectedMinute, reminderEnabled)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {
            Text(if (isSaving) "Saving…" else if (initialReminder == null) "Save Reminder" else "Update Reminder")
        }
        OutlinedButton(onClick = onCancel, enabled = !isSaving, modifier = Modifier.fillMaxWidth()) {
            Text(if (initialReminder == null) "Clear Form" else "Cancel Edit")
        }
        statusMessage?.let {
            Text(it, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("Saved Reminders", style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth())
        if (reminders.isEmpty()) {
            Text("No saved reminders yet.", modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        }
        reminders.forEach { reminder ->
            Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(reminder.medicationName, style = MaterialTheme.typography.titleSmall)
                Text(String.format(Locale.getDefault(), "%02d:%02d · %s", reminder.hour, reminder.minute,
                    if (reminder.reminderEnabled) "Enabled" else "Disabled"))
                Row {
                    TextButton(onClick = { onEdit(reminder) }, enabled = !isSaving) { Text("Edit") }
                    TextButton(onClick = { onDelete(reminder) }, enabled = !isSaving) { Text("Delete") }
                }
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            is24Hour = false
        )

        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select reminder time") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedHour = timePickerState.hour
                        selectedMinute = timePickerState.minute
                        showTimePicker = false
                    }
                ) {
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
