package com.fit5046.health304

import android.app.Application
import android.util.Log
import com.fit5046.health304.data.AppDatabase
import com.fit5046.health304.data.SensorReading
import com.fit5046.health304.sensors.SensorDataSimulator
import com.fit5046.health304.reminders.MedicationAlarmScheduler
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/** One process-level observer, not one observer per screen/recomposition. */
class HealthApplication : Application() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var sensorSimulator: SensorDataSimulator

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        // One process-wide simulator; Firebase binding stops on logout and switches UID.
        sensorSimulator = SensorDataSimulator(this)
        sensorSimulator.bindToFirebase { sample ->
            AppDatabase.getDatabase(this).sensorReadingDAO().insert(
                SensorReading(userId = sample.userId, timestamp = sample.timestamp,
                    heartRate = sample.heartRate, steps = sample.steps)
            )
        }
        val scheduler = MedicationAlarmScheduler(this)
        val auth = FirebaseAuth.getInstance()
        val uid = MutableStateFlow(auth.currentUser?.uid)
        auth.addAuthStateListener {
            if (uid.value != it.currentUser?.uid) scheduler.cancelTracked()
            uid.value = it.currentUser?.uid
        }
        scope.launch {
            uid.flatMapLatest { account ->
                if (account == null) flowOf(emptyList()) else AppDatabase.getDatabase(this@HealthApplication)
                    .medicationReminderDAO().getReminders(account)
            }.catch { Log.e(MedicationAlarmScheduler.TAG, "Could not observe saved reminders", it) }
                .collect { scheduler.sync(it) }
        }
    }
}
