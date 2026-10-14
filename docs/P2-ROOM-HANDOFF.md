# P2 Room integration — 12 October 2026

## Implemented

- HealthPreferences → HealthPreferencesDAO → HealthPreferencesRepository → HealthPreferencesViewModel.
- MedicationReminder → MedicationReminderDAO → MedicationReminderRepository → MedicationReminderViewModel.
- One AppDatabase, now version 3 (two new tables); migration 1 → 3 preserves existing users.
- All personal records have a Firebase UID `userId`; medication update/delete also check ownership in SQL.
- Profile loads saved values, saves to Room, and cancels without writing. DatePicker has a calendar icon and rejects future dates.
- Medication supports create, list, edit, enable/disable via edit, and delete with confirmation.
- P2DataRoutes wires the screens to ViewModels. Compose previews remain independent of Firebase/database creation.

## Integration with P1/P3/P4

On 13 October, P1's real Firebase email/password registration and login were merged from main. P2 routes use the resulting Firebase UID; logout calls P1's AuthViewModel to clear the session. Without `FirebaseAuth.currentUser`, P2 shows a sign-in message and refuses writes; it never invents a shared test UID. Real-account UI checks below still need to be completed with the user's test accounts.

P1 can observe `healthPreferencesDAO().getPreferences(uid)` for health goal/activity level. P4 can observe `medicationReminderDAO().getReminders(uid)` for account-scoped search. These flows emit after Room writes. P3 should reuse these records rather than keep separate sample medication data.

If another branch adds Room tables, reconcile the combined entities, version and migration paths before merging. Do not downgrade the schema or remove existing tables.

## Manual verification after Firebase login is merged

1. Sign in as account A; save preferences. Leave/reopen the page and restart the app: values should remain.
2. Change preferences then Cancel: reopen and confirm the original values remain.
3. Create two medication reminders; edit one time and disable it; confirm the list updates.
4. Cancel deletion, then confirm deletion: only the selected reminder should disappear.
5. Sign out and sign in as account B: A's records must not appear. Switch back to A: A's records remain.
6. Try blank forms and a future birth date: validation must prevent a write.

`P2RoomTest` checks real SQLite account isolation, CRUD and migration on an emulator using `./gradlew connectedDebugAndroidTest`.

## Remaining scheduled work

14 October update: AlarmManager scheduling/notifications, notification and exact-alarm permission UI, cancel/reschedule on edits/deletes/logout, and Logcat messages are now implemented. See P2-D3-TESTING.md for actual notification delivery verification; that manual check is still pending.

The real Fitbit CSV subset and SensorDataSimulator are implemented and tested. Production insertion remains pending P1's sensor_readings table/DAO and the steps interpretation. See SENSOR-DATA-SOURCE.md; do not create a duplicate sensor table.

For the AI declaration, record this assistance with Room entities/DAOs, repositories/ViewModels, account filtering, migration, UI wiring and tests, together with your own review and adjustments.
