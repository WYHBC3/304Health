
package com.fit5046.health304.ui.screens

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

import com.google.firebase.auth.FirebaseAuth
import com.fit5046.health304.data.MedicationReminder
import com.fit5046.health304.repository.MedicationReminderRepository

import kotlinx.coroutines.flow.flowOf

@Composable
fun SearchDataRoute(
    onBackClick: () -> Unit = {},
    onMoodClick: () -> Unit = {},
    onMedicationClick: () -> Unit = {},
    onExerciseClick: () -> Unit = {},
    onHealthDataClick: () -> Unit = {}
) {
    // Get the application context
    val application =
        LocalContext.current.applicationContext as Application

    // Identify the currently signed-in Firebase user
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Reuse P2's Room repository
    val repository = remember(application) {
        MedicationReminderRepository(application)
    }

    // Only request records belonging to the signed-in user
    val reminderFlow = remember(repository, userId) {
        if (userId != null) {
            repository.getReminders(userId)
        } else {
            flowOf(emptyList<MedicationReminder>())
        }
    }

    key(userId) {
        // Automatically update UI when Room records change
        val medicationReminders by reminderFlow.collectAsState(
            initial = emptyList()
        )

        SearchScreen(
            medicationReminders = medicationReminders,
            onBackClick = onBackClick,
            onMoodClick = onMoodClick,
            onMedicationClick = onMedicationClick,
            onExerciseClick = onExerciseClick,
            onHealthDataClick = onHealthDataClick
        )
    }
}
