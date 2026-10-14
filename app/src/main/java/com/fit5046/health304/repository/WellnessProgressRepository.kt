package com.fit5046.health304.repository

import android.app.Application
import com.fit5046.health304.data.AppDatabase
import com.fit5046.health304.data.WellnessProgress
import kotlinx.coroutines.flow.Flow

class WellnessProgressRepository(
    application: Application
) {

    private val dao =
        AppDatabase
            .getDatabase(application)
            .wellnessProgressDAO()

    fun getProgress(
        userId: String,
        dateKey: String
    ): Flow<WellnessProgress?> {

        return dao.getProgress(
            userId,
            dateKey
        )
    }

    suspend fun saveProgress(
        progress: WellnessProgress
    ) {

        dao.saveProgress(
            progress
        )
    }

    suspend fun deleteProgress(
        userId: String,
        dateKey: String
    ) {

        dao.deleteProgress(
            userId,
            dateKey
        )
    }
}