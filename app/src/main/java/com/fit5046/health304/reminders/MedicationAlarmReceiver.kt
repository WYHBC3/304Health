package com.fit5046.health304.reminders

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fit5046.health304.MainActivity
import com.fit5046.health304.R
import com.fit5046.health304.data.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicationAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != MedicationAlarmScheduler.ACTION_REMIND) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val uid = intent.getStringExtra("userId") ?: return@launch
                val id = intent.getLongExtra("id", -1)
                if (FirebaseAuth.getInstance().currentUser?.uid != uid) {
                    Log.d(MedicationAlarmScheduler.TAG, "Skipped id=$id: account changed")
                    return@launch
                }
                val reminder = AppDatabase.getDatabase(context).medicationReminderDAO().getReminder(id, uid)
                if (reminder == null || !reminder.reminderEnabled || reminder.hour != intent.getIntExtra("hour", -1) || reminder.minute != intent.getIntExtra("minute", -1)) {
                    Log.d(MedicationAlarmScheduler.TAG, "Skipped id=$id: deleted, disabled or edited")
                    return@launch
                }
                val scheduler = MedicationAlarmScheduler(context)
                if (scheduler.notificationsAllowed()) {
                    val openApp = PendingIntent.getActivity(context, 0,
                        Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                    val notification = NotificationCompat.Builder(context, MedicationAlarmScheduler.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_medication_notification)
                        .setContentTitle("Medication reminder")
                        .setContentText("Time for ${reminder.medicationName}.")
                        .setContentIntent(openApp).setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH).build()
                    // Recheck after the database read: never reveal another account's reminder.
                    if (FirebaseAuth.getInstance().currentUser?.uid == uid) {
                        NotificationManagerCompat.from(context).notify(MedicationAlarmScheduler.notificationTag(uid, id), 0, notification)
                        Log.d(MedicationAlarmScheduler.TAG, "Notification delivered id=$id")
                    }
                } else Log.d(MedicationAlarmScheduler.TAG, "Notification blocked by permission/channel id=$id")
                scheduler.schedule(reminder) // Daily repetition; re-compute tomorrow's local time.
            } catch (error: Exception) {
                Log.e(MedicationAlarmScheduler.TAG, "Reminder delivery failed", error)
            } finally { pendingResult.finish() }
        }
    }
}
