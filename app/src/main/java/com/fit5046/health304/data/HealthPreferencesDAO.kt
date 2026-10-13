package com.fit5046.health304.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthPreferencesDAO {
    @Query("SELECT * FROM health_preferences WHERE userId = :userId")
    fun getPreferences(userId: String): Flow<HealthPreferences?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(preferences: HealthPreferences)
}
