# P2 D3: alarms and sensor simulation

## Medication notifications

One Application-level Room observer schedules only the signed-in Firebase account's enabled reminders.
Saving/editing/disabling/deleting changes the Room list and cancels/replaces registered alarms.
Logout cancels alarms and their visible medication notifications. At delivery the receiver re-reads
the current record and UID, skipping deleted/disabled/edited or other-account entries.
Daily recurrence re-computes the next local date/time, including daylight-saving changes.
Reboot, time-zone/clock changes and exact-alarm permission grants restore saved alarms.

Android 13+ notification access is requested using Meds → Enable notifications.
Android 12+ exact-alarm access uses Meds → Allow precise reminders → system settings.
If exact alarms are denied, AlarmManager uses an inexact alarm and the UI warns of possible delays.
If notifications are denied, the record is still saved but a clear permission message is shown.
Use SCHEDULE_EXACT_ALARM, not the restricted auto-granted USE_EXACT_ALARM permission.

For demo:
1. Sign in; open Meds; grant notifications and allow precise reminders.
2. Create a reminder for two minutes in the future, switch on, save, then go Home/background the app.
3. Observe the notification and Logcat `MedicationAlarm` schedule/delivery messages.
4. Edit a future reminder to a later time: no notification should arrive at the old time.
5. Disable or delete it: its alarm must be cancelled (visible in Logcat).
6. Switch accounts: the previous account must not receive notifications.
7. Deny either permission: saving must still work and the relevant warning must appear.

Force-stopping an Android app cancels its alarms until it is opened again; this is OS behaviour.
An ordinary background app does not require the Meds screen to remain open.

## Sensor replay

Real Kaggle source, extraction and both step interpretations are documented in SENSOR-DATA-SOURCE.md.
The simulator exposes an injectable suspend insert callback and optional Firebase session binding.
It runs every 5 seconds, wraps at the CSV end and logs successful insertions as SensorDataSimulator.
P1 confirmed raw per-minute INTERVAL steps and current timestamps; Firebase binding now fixes that mode.
HealthApplication now binds the simulator to P1's sensorReadingDAO().insert after Firebase login.
Replay tests use an accelerated collecting callback; an additional default-five-second test writes
to real in-memory Room sensor_readings and verifies raw steps, current timestamps, SUM and isolation.

## Verification

Run `./gradlew :app:testDebugUnitTest` for scheduling/DST/CSV validation.
Run `./gradlew :app:connectedDebugAndroidTest` for real Room and sensor replay tests.
Actual OS notification delivery must additionally be confirmed using the signed-in manual demo above.

Verified 14 October: debug build passed; five D3 unit tests passed; six selected emulator tests
(four Room/migration tests and two sensor replay tests) passed before DAO integration. Junzhe confirmed that the signed-in
manual notification test passed on 14 October (notification permissions enabled and popup received).

After DAO integration: latest-main debug build and unit tests passed, and all seven selected emulator
tests passed, including default-five-second insertion into sensor_readings, current timestamps,
raw minute steps, observeStepsSince SUM and account isolation. Migrations 1/3 -> 5 validate against
the full combined Room schema without deleting the previous records.

Record AI assistance with these implementations, tests and real-data extraction in the A4 declaration;
include your review, manual test results, and any changes made after testing.
