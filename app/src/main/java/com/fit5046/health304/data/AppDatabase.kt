package com.fit5046.health304.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * The single Room database shared by the whole app.
 *
 * TEAM RULE: when you add a new table,
 *   1. add your Entity class to the `entities` array below,
 *   2. add an abstract function that returns your DAO,
 *   3. increase `version` by 1.
 */
@Database(
    entities = [
        User::class,
        HealthPreferences::class,
        MedicationReminder::class,
        MoodEntry::class,
        SensorReading::class,
        Recommendation::class,
        WellnessProgress::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDAO(): UserDAO
    abstract fun healthPreferencesDAO(): HealthPreferencesDAO
    abstract fun medicationReminderDAO(): MedicationReminderDAO
    abstract fun moodDAO(): MoodDAO
    abstract fun sensorReadingDAO(): SensorReadingDAO
    abstract fun recommendationDAO(): RecommendationDAO
    abstract fun wellnessProgressDAO(): WellnessProgressDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Two new tables: preserve existing users when upgrading from P1's schema.
        val MIGRATION_1_3 = object : Migration(1, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS health_preferences (userId TEXT NOT NULL PRIMARY KEY, dateOfBirthMillis INTEGER NOT NULL, healthGoal TEXT NOT NULL, activityLevel TEXT NOT NULL, updatedAt INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS medication_reminders (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, medicationName TEXT NOT NULL, hour INTEGER NOT NULL, minute INTEGER NOT NULL, reminderEnabled INTEGER NOT NULL, createdAt INTEGER NOT NULL)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_medication_reminders_userId ON medication_reminders(userId)")
            }
        }

        // Preserve existing P2 user/preferences/medication data on v3 -> v4.
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS mood_entries (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        dateMillis INTEGER NOT NULL,
                        mood TEXT NOT NULL,
                        factors TEXT NOT NULL,
                        notes TEXT NOT NULL
                    )""".trimIndent()
                )
            }
        }

        // Preserve existing app data when P1 adds sensor readings and recommendations.
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS sensor_readings (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, timestamp INTEGER NOT NULL, heartRate INTEGER NOT NULL, steps INTEGER NOT NULL)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_sensor_readings_userId ON sensor_readings(userId)")
                db.execSQL("CREATE TABLE IF NOT EXISTS recommendations (userId TEXT NOT NULL PRIMARY KEY, title TEXT NOT NULL, message TEXT NOT NULL, category TEXT NOT NULL, reason TEXT NOT NULL, createdAt INTEGER NOT NULL)")
            }
        }


        // Add Daily Wellness Progress tracking while preserving existing v5 data.
        val MIGRATION_5_6 = object : Migration(5, 6) {

            override fun migrate(
                db: SupportSQLiteDatabase
            ) {

                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS wellness_progress (
                userId TEXT NOT NULL,
                dateKey TEXT NOT NULL,
                medicationCompleted INTEGER NOT NULL,
                exerciseCompleted INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL,
                PRIMARY KEY(userId, dateKey)
            )
            """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "health304_database"
                )
                    .addMigrations(MIGRATION_1_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    // Team's development fallback for other unsupported schema changes.
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
