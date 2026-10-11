# 304Health

FIT5046 Assessment 4 – Group 304. A low-cost health and wellness tracker (SDG 3) built with Kotlin and Jetpack Compose.

## Team

| Role | Member | Main areas |
|---|---|---|
| P1 | Yuanhe Wu | Login / Sign-up (Firebase Auth + Google), WorkManager & context-aware rules, project setup |
| P2 | Junzhe Zhang | Health Preferences form, Medication Reminder + AlarmManager, sensor data simulator |
| P3 | Lezhe Zhou | Home Dashboard, Daily Wellness Progress, Mood Diary (Room + LazyColumn), navigation |
| P4 | Zixiang Gong | Exercise Library (Retrofit), Health Trends report, Search |

## First-time setup

1. Connect to Monash VPN, then clone or update:
   `git remote set-url origin https://git.infotech.monash.edu/fit5046/fit5046-s2-2026/304.git`
2. Open the project in Android Studio and **Sync**. Do **not** accept the AGP upgrade prompt.
3. `app/google-services.json` is already in the repo. For Google Sign-In to work on **your** machine, run
   `./gradlew signingReport`, copy the **debug SHA1**, and add it in
   Firebase Console → Project settings → Your apps → Add fingerprint.

## Project structure

```
com.fit5046.health304
├── data/        Room: Entities, DAOs, AppDatabase
├── repository/  Repositories (wrap DAOs / Retrofit / Firebase)
├── viewmodel/   ViewModels used by the screens
├── network/     Retrofit interface + object      (P4)
├── model/       Retrofit data classes            (P4)
├── ui/          screens, components, theme
└── MainActivity.kt
```

Follow the pattern of `User` → `UserDAO` → `AppDatabase` → `UserRepository` → `UserViewModel` (same as Week 7 Room lab).

## Room rules

- There is **one** database: `AppDatabase`. Do not create another one.
- To add a table: add your Entity to `entities = [...]`, add `abstract fun yourDAO(): YourDAO`, and **increase `version` by 1**.
- Every table that stores personal data must have a `userId: String` column (the Firebase UID), so each account only sees its own records. Get it with `FirebaseAuth.getInstance().currentUser?.uid`.
- If the app crashes with *"Room cannot verify the data integrity"*, uninstall the app from the emulator and run again.

## Git workflow

- Never commit directly to `main`. Work on your own branch (`Yuanhe`, `Junzhe`, `Lezhe`, `ZiXiangGong`).
- Before starting each day: `git checkout main && git pull`, then `git checkout <your-branch> && git merge main`.
- Commit small and often with clear messages, e.g. `feat: save mood entries to Room`, `fix: crash when list is empty`.
- Merge into `main` at least once a day via a Merge Request; make sure the app builds before merging.
- Do **not** change `gradle/libs.versions.toml` or `build.gradle.kts` without telling P1 first.