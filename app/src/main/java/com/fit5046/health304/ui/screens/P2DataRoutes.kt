package com.fit5046.health304.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.fit5046.health304.reminders.MedicationAlarmScheduler

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
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    val scheduler = remember { MedicationAlarmScheduler(context) }
    var permissionRevision by remember { mutableIntStateOf(0) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionRevision++
        scheduler.sync(model.reminders.value)
    }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionRevision++
                scheduler.sync(model.reminders.value)
            }
        }
        lifecycle.lifecycle.addObserver(observer)
        onDispose { lifecycle.lifecycle.removeObserver(observer) }
    }
    val notificationAllowed = remember(permissionRevision) { scheduler.notificationsAllowed() }
    val exactAllowed = remember(permissionRevision) { scheduler.canScheduleExact() }
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
                            message = when {
                                !enabled -> "Reminder saved with notifications disabled."
                                !scheduler.notificationsAllowed() -> "Reminder saved. Enable notifications to receive it."
                                !scheduler.canScheduleExact() -> "Reminder saved. Allow precise reminders to avoid timing delays."
                                else -> "Reminder saved. Daily notification scheduled."
                            }
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (error: Exception) {
                            message = error.message ?: "Could not save reminder. Please try again."
                        } finally { saving = false }
                    }
                },
                isSaving = saving,
                statusMessage = message ?: if (uid == null) "Sign in to save your reminders." else null,
                permissionContent = {
                    Column(Modifier.padding(bottom = 12.dp)) {
                        if (!notificationAllowed) {
                            Text("Notifications are off. Saved reminders cannot notify you yet.")
                            TextButton(onClick = {
                                if (Build.VERSION.SDK_INT >= 33) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                else context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName))
                            }) { Text("Enable notifications") }
                            TextButton(onClick = {
                                context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName))
                            }) { Text("Notification settings") }
                        }
                        if (!exactAllowed && Build.VERSION.SDK_INT >= 31) {
                            Text("Precise reminders are off. Notifications may arrive late.")
                            TextButton(onClick = {
                                context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                    Uri.parse("package:${context.packageName}")))
                            }) { Text("Allow precise reminders") }
                        }
                    }
                }
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
