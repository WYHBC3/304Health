package com.fit5046.health304.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fit5046.health304.data.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Restore saved alarms after reboot, clock/time-zone changes or an exact-alarm grant. */
class ReminderRestoreReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val rows = if (uid == null) emptyList() else AppDatabase.getDatabase(context)
                    .medicationReminderDAO().getReminders(uid).first()
                MedicationAlarmScheduler(context).sync(rows)
                Log.d(MedicationAlarmScheduler.TAG, "Restored alarms after ${intent.action}")
            } catch (error: Exception) {
                Log.e(MedicationAlarmScheduler.TAG, "Could not restore alarms", error)
            } finally { pending.finish() }
        }
    }
}
