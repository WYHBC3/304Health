"""Read the public Kaggle zip and emit an apply_patch for the small Android asset.

Usage: python3 tools/prepare_sensor_csv.py /path/fitbit.zip | apply_patch
No random or invented readings; joins one participant's real per-minute readings.
"""
import collections
import csv
import datetime
import io
import itertools
import pathlib
import sys
import zipfile

archive = zipfile.ZipFile(sys.argv[1])
prefix = 'mturkfitbit_export_4.12.16-5.12.16/Fitabase Data 4.12.16-5.12.16/'
heart_rates = collections.defaultdict(list)
with archive.open(prefix + 'heartrate_seconds_merged.csv') as raw:
    rows = csv.DictReader(io.TextIOWrapper(raw))
    first = next(rows)
    participant, day = first['Id'], first['Time'].split(' ')[0]
    for row in itertools.chain([first], rows):
        if row['Id'] != participant or row['Time'].split(' ')[0] != day:
            continue
        minute = datetime.datetime.strptime(row['Time'], '%m/%d/%Y %I:%M:%S %p').replace(second=0)
        heart_rates[minute].append(int(row['Value']))

steps = {}
with archive.open(prefix + 'minuteStepsNarrow_merged.csv') as raw:
    for row in csv.DictReader(io.TextIOWrapper(raw)):
        if row['Id'] == participant and row['ActivityMinute'].split(' ')[0] == day:
            minute = datetime.datetime.strptime(row['ActivityMinute'], '%m/%d/%Y %I:%M:%S %p')
            steps[minute] = int(row['Steps'])
cumulative, total = {}, 0
for minute, value in sorted(steps.items()):
    total += value
    cumulative[minute] = total
joined = sorted(set(heart_rates) & set(steps))
active = next(i for i, minute in enumerate(joined) if steps[minute] > 0)
selected = joined[max(0, active - 5):max(0, active - 5) + 120]
assert len(selected) == 120
target = pathlib.Path(__file__).resolve().parent.parent / 'app/src/main/assets/sensor_data.csv'
print('*** Begin Patch')
print(f'*** Add File: {target}')
print('+sourceTime,heartRate,stepsInterval,stepsCumulative')
for minute in selected:
    bpm = round(sum(heart_rates[minute]) / len(heart_rates[minute]))
    print('+' + ','.join(map(str, [minute.isoformat(), bpm, steps[minute], cumulative[minute]])))
print('*** End Patch')
