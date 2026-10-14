package com.fit5046.health304.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit5046.health304.data.MoodEntry
import com.fit5046.health304.repository.MoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MoodViewModel(application: Application) : AndroidViewModel(application) {

    private val moodRepository: MoodRepository =
        MoodRepository(application)

    fun getMoodEntries(userId: String): Flow<List<MoodEntry>> =
        moodRepository.getMoodEntries(userId)

    fun insertMoodEntry(moodEntry: MoodEntry) =
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.insert(moodEntry)
        }

    fun updateMoodEntry(moodEntry: MoodEntry) =
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.update(moodEntry)
        }

    fun deleteMoodEntry(moodEntry: MoodEntry) =
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.delete(moodEntry)
        }
}