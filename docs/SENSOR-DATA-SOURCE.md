# Real Fitbit sample and replay contract

Source: https://www.kaggle.com/datasets/arashnic/fitbit (FitBit Fitness Tracker Data, MÖBIUS).
Downloaded the public dataset archive via Kaggle on 14 October 2026.

Files: `mturkfitbit_export_4.12.16-5.12.16/Fitabase Data 4.12.16-5.12.16/heartrate_seconds_merged.csv`
and `minuteStepsNarrow_merged.csv`.

The asset contains 120 matched minute samples from participant 2022484408 on 12 April 2016.
Heart-rate readings are grouped by minute and averaged, then rounded to an integer BPM.
Steps are joined on the same participant and minute. Minutes without both sources are omitted;
source times may therefore contain gaps. No random heart rates or steps are generated.

Columns: `sourceTime,heartRate,stepsInterval,stepsCumulative`.
`stepsInterval` is the original number of steps in that source minute.
`stepsCumulative` sums that participant's available minute-step records from the start of the source day,
including minutes omitted from the heart-rate join.

Each source minute is replayed every five real seconds. The DB timestamp is the CURRENT replay
time in milliseconds, not the historical Fitbit date. This is an accelerated demo, not a live wearable.
At the end, replay starts over. In cumulative mode the source-day total resets at wrap-around;
consumers must handle resets and MUST NOT sum cumulative samples. In interval mode steps can be
summed for the simulated timeline, but repeated cycles are deliberately repeated source observations.

P1 still needs to confirm INTERVAL versus CUMULATIVE and merge the SensorReading/DAO table.
The simulator is implemented and tested with an injected persistence callback, but is not yet bound
in HealthApplication. After the interface is merged, keep one simulator instance in Application and call
`bindToFirebase(mode) { sample -> dao.insert(SensorReading(userId = sample.userId,
timestamp = sample.timestamp, heartRate = sample.heartRate, steps = sample.steps)) }`.
Use the actual DAO accessor P1 commits, not a second database or duplicate table.

Logout cancels the loop; account switching starts a loop with the new UID. Rebinding the same
active UID/mode does not launch a second loop. Ordinary process termination stops simulation;
it resumes on app restart. It is not a five-second background service guaranteed after process death.

Reproduce the asset with `python3 tools/prepare_sensor_csv.py /path/fitbit.zip | apply_patch`.
Keep the raw archive outside the repository.
