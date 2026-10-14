package com.fit5046.health304.repository

import android.app.Application
import com.fit5046.health304.data.AppDatabase
import com.fit5046.health304.data.MoodDAO
import com.fit5046.health304.data.MoodEntry
import kotlinx.coroutines.flow.Flow

class MoodRepository(application: Application) {

    private val moodDao: MoodDAO =
        AppDatabase.getDatabase(application).moodDAO()

    fun getMoodEntries(userId: String): Flow<List<MoodEntry>> =
        moodDao.getMoodEntries(userId)

    suspend fun insert(moodEntry: MoodEntry) {
        moodDao.insertMoodEntry(moodEntry)
    }

    suspend fun update(moodEntry: MoodEntry) {
        moodDao.updateMoodEntry(moodEntry)
    }

    suspend fun delete(moodEntry: MoodEntry) {
        moodDao.deleteMoodEntry(moodEntry)
    }
}