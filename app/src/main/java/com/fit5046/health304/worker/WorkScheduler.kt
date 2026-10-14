package com.fit5046.health304.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/** Starts and stops the context-aware background work. */
object WorkScheduler {

    private const val TAG = "WorkScheduler"
    private const val PERIODIC_WORK = "context_aware_periodic"
    private const val ONE_TIME_WORK = "context_aware_now"

    /** Called after sign-in: refresh now, then keep refreshing every 15 minutes. */
    fun start(context: Context) {
        runNow(context)

        // 15 minutes is the minimum interval WorkManager allows for periodic work
        val request = PeriodicWorkRequestBuilder<ContextAwareWorker>(15, TimeUnit.MINUTES)
            // runNow() already handles the first run, so the periodic one starts 15 min later
            .setInitialDelay(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        // KEEP: if it is already scheduled, do not create a duplicate
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        Log.d(TAG, "Periodic context-aware work scheduled (every 15 min)")
    }

    /** One-off run, e.g. right after sign-in or when the user taps Refresh. */
    fun runNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<ContextAwareWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_WORK,
            ExistingWorkPolicy.REPLACE,
            request
        )
        Log.d(TAG, "One-time context-aware work enqueued")
    }

    /** Called on logout so no work runs for a signed-out user. */
    fun stop(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK)
        WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_WORK)
        Log.d(TAG, "Context-aware work cancelled")
    }
}