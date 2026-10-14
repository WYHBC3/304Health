package com.fit5046.health304.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorReadingDAO {

    @Insert
    suspend fun insert(reading: SensorReading)

    // Used by the WorkManager worker (one-off read in the background)
    @Query("SELECT * FROM sensor_readings WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getLatest(userId: String, limit: Int): List<SensorReading>

    // Used by UI screens (Home / Health Trends) for live updates
    @Query("SELECT * FROM sensor_readings WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(userId: String, limit: Int): Flow<List<SensorReading>>

    @Query("SELECT COALESCE(SUM(steps), 0) FROM sensor_readings WHERE userId = :userId AND timestamp >= :since")
    fun observeStepsSince(userId: String, since: Long): Flow<Int>
}