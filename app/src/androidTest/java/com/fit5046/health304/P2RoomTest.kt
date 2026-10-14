package com.fit5046.health304

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fit5046.health304.data.AppDatabase
import com.fit5046.health304.data.HealthPreferences
import com.fit5046.health304.data.MedicationReminder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

/** Real SQLite checks; never reads or writes the user's app database. */
@RunWith(AndroidJUnit4::class)
class P2RoomTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun moodSchemaUpgradeKeepsExistingPreferencesAndMedication() = runBlocking {
        val name = "p2-v3-upgrade-${UUID.randomUUID()}.db"
        val path = context.getDatabasePath(name)
        path.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(path, null).use { legacy ->
            legacy.execSQL("CREATE TABLE users (uid TEXT NOT NULL PRIMARY KEY, firstName TEXT NOT NULL, lastName TEXT NOT NULL, email TEXT NOT NULL, createdAt INTEGER NOT NULL)")
            legacy.execSQL("CREATE TABLE health_preferences (userId TEXT NOT NULL PRIMARY KEY, dateOfBirthMillis INTEGER NOT NULL, healthGoal TEXT NOT NULL, activityLevel TEXT NOT NULL, updatedAt INTEGER NOT NULL)")
            legacy.execSQL("CREATE TABLE medication_reminders (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, medicationName TEXT NOT NULL, hour INTEGER NOT NULL, minute INTEGER NOT NULL, reminderEnabled INTEGER NOT NULL, createdAt INTEGER NOT NULL)")
            legacy.execSQL("CREATE INDEX index_medication_reminders_userId ON medication_reminders(userId)")
            legacy.execSQL("INSERT INTO health_preferences VALUES ('account-a', 946684800000, 'Improve sleep', 'Low', 1)")
            legacy.execSQL("INSERT INTO medication_reminders VALUES (1, 'account-a', 'Test Vitamin', 8, 0, 1, 1)")
            legacy.version = 3
        }
        val db = Room.databaseBuilder(context, AppDatabase::class.java, name)
            .addMigrations(AppDatabase.MIGRATION_3_4).build()
        try {
            assertEquals("Improve sleep", db.healthPreferencesDAO().getPreferences("account-a").first()?.healthGoal)
            assertEquals("Test Vitamin", db.medicationReminderDAO().getReminders("account-a").first().single().medicationName)
            assertTrue(db.moodDAO().getMoodEntries("account-a").first().isEmpty())
        } finally { db.close(); context.deleteDatabase(name) }
    }

    @Test fun preferencesPersistPerAccountAndSaveReplacesOnlyThatAccount() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val dao = db.healthPreferencesDAO()
            dao.save(HealthPreferences("account-a", -315619200000L, "Improve fitness", "Low"))
            dao.save(HealthPreferences("account-b", 946684800000L, "Manage stress", "High"))
            dao.save(HealthPreferences("account-a", -315619200000L, "Improve sleep", "Moderate"))
            assertEquals("Improve sleep", dao.getPreferences("account-a").first()?.healthGoal)
            assertEquals("Manage stress", dao.getPreferences("account-b").first()?.healthGoal)
            assertNull(dao.getPreferences("unknown-account").first())
        } finally { db.close() }
    }

    @Test fun medicationCrudRejectsOtherAccounts() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val dao = db.medicationReminderDAO()
            val id = dao.insert(MedicationReminder(userId = "account-a", medicationName = "Vitamin D", hour = 8, minute = 0, reminderEnabled = true))
            dao.insert(MedicationReminder(userId = "account-b", medicationName = "Vitamin B", hour = 9, minute = 0, reminderEnabled = true))
            assertEquals(1, dao.getReminders("account-a").first().size)
            assertEquals(0, dao.update(id, "account-b", "Wrong owner", 10, 0, false))
            assertEquals(0, dao.delete(id, "account-b"))
            assertEquals(1, dao.update(id, "account-a", "Vitamin D", 10, 30, false))
            assertFalse(dao.getReminders("account-a").first().single().reminderEnabled)
            assertEquals(1, dao.delete(id, "account-a"))
            assertTrue(dao.getReminders("account-a").first().isEmpty())
            assertEquals(1, dao.getReminders("account-b").first().size)
        } finally { db.close() }
    }

    @Test fun migrationKeepsExistingUsersAndRoomValidatesNewSchema() = runBlocking {
        val name = "p2-migration-${UUID.randomUUID()}.db"
        val path = context.getDatabasePath(name)
        path.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(path, null).use { legacy ->
            legacy.execSQL("CREATE TABLE users (uid TEXT NOT NULL PRIMARY KEY, firstName TEXT NOT NULL, lastName TEXT NOT NULL, email TEXT NOT NULL, createdAt INTEGER NOT NULL)")
            legacy.execSQL("INSERT INTO users VALUES ('account-a', 'Test', 'User', 'test@example.com', 1)")
            legacy.version = 1
        }
        val db = Room.databaseBuilder(context, AppDatabase::class.java, name)
            .addMigrations(AppDatabase.MIGRATION_1_3, AppDatabase.MIGRATION_3_4).build()
        try {
            assertEquals("Test", db.userDAO().getUser("account-a").first()?.firstName)
            db.healthPreferencesDAO().save(HealthPreferences("account-a", 946684800000L, "Improve sleep", "Low"))
            assertEquals("Low", db.healthPreferencesDAO().getPreferences("account-a").first()?.activityLevel)
        } finally { db.close(); context.deleteDatabase(name) }
    }
}
