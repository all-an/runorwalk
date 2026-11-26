# ROADMAP — runorwalk

Status legend: `- [ ]` todo, `- [x]` done. Every step ends with passing unit tests (`gradlew testDebugUnitTest`) and, from Part 3, a runnable APK.

## Part 1 — Project Skeleton
### 1.0 Gradle Android project
- [x] `settings.gradle.kts`, root and `app` build files, `gradle.properties`, `.gitignore`.
- [x] Zero dependency policy (no AndroidX, no map, no Play Services); JUnit 4 for tests.
- [x] `AGENTS.md` with KISS, clean code and mandatory unit tests.
### 1.1 Gradle wrapper & first build
- [ ] Generate wrapper, `assembleDebug` succeeds.
- [ ] `testDebugUnitTest` passes in CI-like local run.

## Part 2 — Pure Logic (`calc`, JVM tested)
### 2.0 Geo & formatting
- [x] `TrackPoint` record and haversine `Geo.distanceMeters`.
- [x] `PaceFormat`: parse/format `m:ss` pace, km, duration.
### 2.1 Workout session
- [x] `WorkoutSession`: accumulate distance, elapsed time, average pace.
- [x] Noise filter: accuracy, minimum step, max speed, timestamp order.
- [x] Current pace over a rolling 15 s window.
### 2.2 Zone 2
- [x] `ZoneTwo` range with validation and `contains`.
- [x] Zone 2 amount: time, distance and % of elapsed time.

## Part 3 — Android App
### 3.0 Tracking service
- [x] Foreground `TrackingService` with `GPS_PROVIDER` updates every 1 s.
- [x] Permissions: fine location, foreground service location, notifications.
- [x] `SessionHolder` shares the live session with the UI.
### 3.1 Main screen
- [x] Big distance, time, current pace, average pace, Zone 2 amount, Start/Stop.
- [x] GPS status line (waiting / tracking / finished).
### 3.2 Zone 2 settings screen
- [x] Type fast and slow limit as `m:ss`, validated, saved in `SharedPreferences`.
- [ ] Show the active Z2 range on the main screen.

## Part 4 — Field Polish
### 4.0 Real-device testing
- [ ] Walk test: distance within ~3% of a known route.
- [ ] Screen-off test: tracking continues in the background.
### 4.1 Usability
- [ ] Keep screen on while tracking (optional setting).
- [ ] Pause/resume button (stationary time excluded).
- [ ] Color the current pace: in Z2 / too fast / too slow.
- [ ] Restore the running session after the activity is killed.

## Part 5 — History (optional)
### 5.0 Save workouts
- [ ] Persist finished workouts (date, distance, time, Z2 amount) in SQLite.
- [ ] Simple history list screen and delete action.
### 5.1 Export
- [ ] Share a workout summary as text/CSV.

## Part 6 — Release
### 6.0 Packaging
- [ ] Release build with R8 minify, signed APK/AAB.
- [ ] Proper launcher icon set and Play Store metadata.
