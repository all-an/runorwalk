# runorwalk

A super simple, lightweight Android app that tracks a **walk or run** using only the phone's **GPS**. No map, no internet, no account.

## Features
- **Distance** (km) and **time** of the workout.
- **Current pace** (last ~15 s) and **average pace**, in min/km.
- **Zone 2 amount**: time, distance and % of the workout spent inside your Zone 2 pace range.
- **Zone 2 settings screen**: type your fast and slow limits as `m:ss` per km (default 6:00–7:00).
- Keeps tracking with the screen off (foreground service with a notification).
- GPS noise filter: ignores inaccurate fixes (> 25 m), jitter (< 3 m) and impossible jumps (> 12 m/s).
- Status line: `Waiting for GPS…`, `Weak GPS (NN m)`, `Tracking`, `Finished`.

## Requirements
- JDK 17+ and the Android SDK (platform 36). Put the SDK path in `local.properties` (git-ignored):
  `sdk.dir=C\:\\Users\\<you>\\AppData\\Local\\Android\\Sdk`
- Android 8.0+ (`minSdk 26`) device with GPS.

## Build, test, install
```powershell
.\gradlew.bat testDebugUnitTest   # JVM unit tests
.\gradlew.bat assembleDebug       # app\build\outputs\apk\debug\app-debug.apk
.\gradlew.bat installDebug        # install on a connected phone (USB debugging) or emulator
```

## Using it
1. Open **Z2 pace** and enter your Zone 2 range, then save.
2. Press **START** and grant **Precise** location (and notifications on Android 13+).
3. Wait for the status to change to `Tracking` (open sky helps), then walk or run.
4. Press **STOP** to finish; the final numbers stay on screen.

**Emulator:** it has no GPS. Use *Extended controls → Location* and play a GPX/KML route to produce movement.

## Project layout
| Path | Purpose |
|---|---|
| `app/src/main/java/com/runorwalk/calc` | Pure Java logic (distance, pace, Zone 2), unit tested on the JVM |
| `app/src/main/java/com/runorwalk/track` | Foreground GPS service and shared live session |
| `app/src/main/java/com/runorwalk/ui` | Main screen, Z2 settings screen |
| `app/src/test` | JUnit 4 tests |

See [AGENTS.md](AGENTS.md) for development guidelines (KISS, clean code, mandatory unit tests) and [ROADMAP.md](ROADMAP.md) for the plan.
