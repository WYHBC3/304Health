package com.fit5046.health304.reminders

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.fit5046.health304.data.MedicationReminder
import com.google.firebase.auth.FirebaseAuth

class MedicationAlarmScheduler(context: Context) {
    private val context = context.applicationContext
    private val alarms = this.context.getSystemService(AlarmManager::class.java)
    private val notifications = this.context.getSystemService(NotificationManager::class.java)
    private val registry = this.context.getSharedPreferences("medication_alarm_registry", Context.MODE_PRIVATE)

    init {
        if (Build.VERSION.SDK_INT >= 26) notifications.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Medication reminders", NotificationManager.IMPORTANCE_HIGH)
                .apply { description = "Daily reminders for your saved medication times" }
        )
    }

    fun canScheduleExact(): Boolean = Build.VERSION.SDK_INT < 31 || alarms.canScheduleExactAlarms()
    fun notificationsAllowed(): Boolean =
        (Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) &&
            NotificationManagerCompat.from(context).areNotificationsEnabled() &&
            (Build.VERSION.SDK_INT < 26 || notifications.getNotificationChannel(CHANNEL_ID)?.importance != NotificationManager.IMPORTANCE_NONE)

    private fun identity(uid: String, id: Long) = Uri.Builder().scheme("health304")
        .authority("medication").appendPath(uid).appendPath(id.toString()).build()

    private fun operation(reminder: MedicationReminder): PendingIntent = PendingIntent.getBroadcast(
        context, 0, Intent(context, MedicationAlarmReceiver::class.java).apply {
            action = ACTION_REMIND
            data = identity(reminder.userId, reminder.id)
            putExtra("userId", reminder.userId)
            putExtra("id", reminder.id)
            putExtra("hour", reminder.hour)
            putExtra("minute", reminder.minute)
        }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    @Synchronized
    fun schedule(reminder: MedicationReminder) {
        if (!reminder.reminderEnabled || FirebaseAuth.getInstance().currentUser?.uid != reminder.userId) return
        val at = nextReminderTime(reminder.hour, reminder.minute)
        val pending = operation(reminder)
        var exact = canScheduleExact()
        try {
            if (exact) alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending)
            else alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending)
        } catch (_: SecurityException) {
            // Permission can be revoked between the check and the actual call.
            exact = false
            alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending)
        }
        val key = identity(reminder.userId, reminder.id).toString()
        registry.edit().putStringSet("scheduled", registry.getStringSet("scheduled", emptySet())!!.toSet() + key).apply()
        Log.d(TAG, "Scheduled id=${reminder.id}, at=$at, exact=$exact")
    }

    @Synchronized
    fun cancelTracked() {
        registry.getStringSet("scheduled", emptySet())!!.toSet().forEach { key ->
            val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
                action = ACTION_REMIND
                data = Uri.parse(key)
            }
            PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)?.let {
                alarms.cancel(it)
                it.cancel()
            }
            notifications.cancel(key, 0)
            Log.d(TAG, "Cancelled $key")
        }
        registry.edit().remove("scheduled").apply()
    }

    @Synchronized
    fun sync(reminders: List<MedicationReminder>) {
        cancelTracked()
        reminders.filter { it.reminderEnabled }.forEach(::schedule)
    }

    companion object {
        const val TAG = "MedicationAlarm"
        const val CHANNEL_ID = "medication_reminders"
        const val ACTION_REMIND = "com.fit5046.health304.MEDICATION_REMINDER"
        fun notificationTag(uid: String, id: Long) = Uri.Builder().scheme("health304")
            .authority("medication").appendPath(uid).appendPath(id.toString()).build().toString()
    }
}
