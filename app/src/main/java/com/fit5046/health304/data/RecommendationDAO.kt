package com.fit5046.health304.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationDAO {

    @Query("SELECT * FROM recommendations WHERE userId = :userId")
    fun observe(userId: String): Flow<Recommendation?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(recommendation: Recommendation)
}