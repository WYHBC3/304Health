package com.fit5046.health304.repository

import android.app.Application
import com.fit5046.health304.data.AppDatabase
import com.fit5046.health304.data.HealthPreferences

class HealthPreferencesRepository(application: Application) {
    private val dao = AppDatabase.getDatabase(application).healthPreferencesDAO()
    fun getPreferences(userId: String) = dao.getPreferences(userId)
    suspend fun save(preferences: HealthPreferences) = dao.save(preferences)
}
