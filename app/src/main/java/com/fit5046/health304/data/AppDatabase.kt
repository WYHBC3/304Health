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
        MedicationReminder::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDAO(): UserDAO
    abstract fun healthPreferencesDAO(): HealthPreferencesDAO
    abstract fun medicationReminderDAO(): MedicationReminderDAO

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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "health304_database"
                )
                    .addMigrations(MIGRATION_1_3)
                    // Team's development fallback for other unsupported schema changes.
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
