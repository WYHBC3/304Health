package com.fit5046.health304.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WellnessProgressDAO {

    @Query(
        """
        SELECT * FROM wellness_progress
        WHERE userId = :userId
        AND dateKey = :dateKey
        LIMIT 1
        """
    )
    fun getProgress(
        userId: String,
        dateKey: String
    ): Flow<WellnessProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(
        progress: WellnessProgress
    )

    @Query(
        """
        DELETE FROM wellness_progress
        WHERE userId = :userId
        AND dateKey = :dateKey
        """
    )
    suspend fun deleteProgress(
        userId: String,
        dateKey: String
    )
}