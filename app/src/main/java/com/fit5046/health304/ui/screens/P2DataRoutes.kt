package com.fit5046.health304.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit5046.health304.data.MedicationReminder
import com.fit5046.health304.viewmodel.HealthPreferencesViewModel
import com.fit5046.health304.viewmodel.MedicationReminderViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun ProfilePreferencesRoute(onSaved: () -> Unit, onCancel: () -> Unit, onLogout: () -> Unit) {
    val model: HealthPreferencesViewModel = viewModel()
    val uid by model.userId.collectAsStateWithLifecycle()
    val preferences by model.preferences.collectAsStateWithLifecycle()
    key(uid) {
        val scope = rememberCoroutineScope()
        var saving by remember { mutableStateOf(false) }
        var message by remember { mutableStateOf<String?>(null) }
        ProfilePreferencesScreen(
            initialPreferences = preferences?.takeIf { it.userId == uid },
            onSave = { date, goal, activity ->
                scope.launch {
                    saving = true
                    message = null
                    try {
                        model.save(date, goal, activity)
                        onSaved()
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (error: Exception) {
                        message = error.message ?: "Could not save preferences. Please try again."
                    } finally { saving = false }
                }
            },
            onCancel = onCancel,
            onLogout = onLogout,
            isSaving = saving,
            statusMessage = message ?: if (uid == null) "Sign in to save your preferences." else null
        )
    }
}

@Composable
fun MedicationReminderRoute() {
    val model: MedicationReminderViewModel = viewModel()
    val uid by model.userId.collectAsStateWithLifecycle()
    val reminders by model.reminders.collectAsStateWithLifecycle()
    key(uid) {
        val scope = rememberCoroutineScope()
        var editing by remember { mutableStateOf<MedicationReminder?>(null) }
        var deleting by remember { mutableStateOf<MedicationReminder?>(null) }
        var formVersion by remember { mutableIntStateOf(0) }
        var saving by remember { mutableStateOf(false) }
        var message by remember { mutableStateOf<String?>(null) }

        key(editing?.id, formVersion) {
            MedicationReminderScreen(
                initialReminder = editing,
                reminders = reminders.filter { it.userId == uid },
                onEdit = { editing = it; message = null },
                onDelete = { deleting = it },
                onCancel = { editing = null; formVersion++; message = null },
                onSave = { name, hour, minute, enabled ->
                    val id = editing?.id
                    scope.launch {
                        saving = true
                        try {
                            model.save(id, name, hour, minute, enabled)
                            editing = null
                            formVersion++
                            message = "Reminder saved. Notifications are not enabled yet."
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (error: Exception) {
                            message = error.message ?: "Could not save reminder. Please try again."
                        } finally { saving = false }
                    }
                },
                isSaving = saving,
                statusMessage = message ?: if (uid == null) "Sign in to save your reminders." else null
            )
        }
        deleting?.let { reminder ->
            AlertDialog(
                onDismissRequest = { if (!saving) deleting = null },
                title = { Text("Delete reminder?") },
                text = { Text("Delete ${reminder.medicationName} from your saved reminders?") },
                confirmButton = {
                    TextButton(enabled = !saving, onClick = {
                        scope.launch {
                            saving = true
                            try {
                                model.delete(reminder.id)
                                if (editing?.id == reminder.id) { editing = null; formVersion++ }
                                deleting = null
                                message = "Reminder deleted."
                            } catch (cancelled: CancellationException) {
                                throw cancelled
                            } catch (error: Exception) {
                                deleting = null
                                message = error.message ?: "Could not delete reminder. Please try again."
                            } finally { saving = false }
                        }
                    }) { Text("Delete") }
                },
                dismissButton = { TextButton(enabled = !saving, onClick = { deleting = null }) { Text("Cancel") } }
            )
        }
    }
}
