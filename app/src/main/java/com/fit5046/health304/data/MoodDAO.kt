package com.fit5046.health304.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDAO {

    @Query(
        """
        SELECT * FROM mood_entries
        WHERE userId = :userId
        ORDER BY dateMillis DESC
        """
    )
    fun getMoodEntries(userId: String): Flow<List<MoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(moodEntry: MoodEntry)

    @Update
    suspend fun updateMoodEntry(moodEntry: MoodEntry)

    @Delete
    suspend fun deleteMoodEntry(moodEntry: MoodEntry)
}