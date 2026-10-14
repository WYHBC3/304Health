package com.fit5046.health304.sensors

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class SimulatedSensorReading(val userId: String, val timestamp: Long,
                                  val heartRate: Int, val steps: Int)

/** Replays real samples; inject P1's DAO insert once the table/steps contract is merged. */
class SensorDataSimulator(context: Context, private val intervalMillis: Long = 5_000L) {
    private val context = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null
    private var activeUserId: String? = null
    private var activeMode: StepsMode? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null

    init { require(intervalMillis > 0) }

    @Synchronized
    fun start(userId: String, mode: StepsMode, isCurrentUser: () -> Boolean = { true },
              insert: suspend (SimulatedSensorReading) -> Unit) {
        require(userId.isNotBlank())
        if (job?.isActive == true && activeUserId == userId && activeMode == mode) return
        stop()
        activeUserId = userId
        activeMode = mode
        job = scope.launch {
            try {
                val samples = SensorCsvReader.read(context.assets.open("sensor_data.csv").reader())
                var index = 0
                Log.d(TAG, "Started replay: rows=${samples.size}, intervalMs=$intervalMillis, mode=$mode")
                while (isActive && isCurrentUser()) {
                    delay(intervalMillis)
                    if (!isCurrentUser()) break
                    val sample = samples[index]
                    val reading = SimulatedSensorReading(userId, System.currentTimeMillis(), sample.heartRate, sample.steps(mode))
                    insert(reading)
                    Log.d(TAG, "Inserted row=$index, timestamp=${reading.timestamp}, heartRate=${reading.heartRate}, steps=${reading.steps}, mode=$mode, source=${sample.sourceTime}")
                    index = (index + 1) % samples.size
                    if (index == 0) Log.d(TAG, "End of CSV: replaying from first row")
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.e(TAG, "Sensor replay stopped: could not read/insert data", error)
            } finally { Log.d(TAG, "Replay stopped") }
        }
    }

    @Synchronized
    fun stop() {
        job?.cancel()
        job = null
        activeUserId = null
        activeMode = null
    }

    /** Call once from Application, not from a composable. Logout cancels the old job. */
    @Synchronized
    fun bindToFirebase(mode: StepsMode, insert: suspend (SimulatedSensorReading) -> Unit) {
        check(authListener == null) { "Simulator is already bound to Firebase." }
        val auth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener {
            val uid = it.currentUser?.uid
            if (uid == null) stop()
            else start(uid, mode, isCurrentUser = { auth.currentUser?.uid == uid }, insert = insert)
        }
        authListener = listener
        auth.addAuthStateListener(listener)
    }

    fun unbind() {
        authListener?.let { FirebaseAuth.getInstance().removeAuthStateListener(it) }
        authListener = null
        stop()
    }

    companion object { const val TAG = "SensorDataSimulator" }
}
