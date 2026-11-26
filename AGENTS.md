# runorwalk

A super simple, lightweight Android app that tracks a **walk or run** using only the **GPS** (no map, no network). It shows **distance**, **time**, **current/average pace**, and the **amount of Zone 2** (time, distance and % spent inside your Zone 2 pace range).

- **Language & Build**: Java 17, Gradle (Kotlin DSL), Android Gradle Plugin, `minSdk 26`.
- **Zero dependencies**: no AndroidX, no Play Services, no map SDK. Android framework APIs only (`LocationManager`, foreground `Service`, `SharedPreferences`). Test dependency: JUnit 4.
- **Tests**: unit tests are mandatory for every feature and bug fix.
- **KISS & Clean Code**: small classes, one responsibility each, no premature abstractions.

## Commands (project root `C:\Users\Public\code\runorwalk`)

```powershell
.\gradlew.bat testDebugUnitTest   # run JVM unit tests
.\gradlew.bat assembleDebug       # build app\build\outputs\apk\debug\app-debug.apk
.\gradlew.bat installDebug        # install on a connected phone (USB debugging on)
```

`local.properties` (git-ignored) must contain `sdk.dir=C\:\\Users\\<you>\\AppData\\Local\\Android\\Sdk`.

## Architecture

| Package | Role | Android types allowed? |
|---|---|---|
| `com.runorwalk.calc` | Pure logic: `Geo`, `TrackPoint`, `PaceFormat`, `ZoneTwo`, `WorkoutSession` | **No** (JVM unit-tested) |
| `com.runorwalk.track` | `TrackingService` (foreground GPS), `SessionHolder` | Yes, keep thin |
| `com.runorwalk.ui` | `MainActivity`, `SettingsActivity`, `ZoneSettings` | Yes, keep thin |

Rules:
- All math, filtering, parsing and formatting lives in `calc` and is unit tested. Activities/services only wire Android APIs to `calc`.
- Units: distance in **meters** (`double`), time in **milliseconds** (`long`), pace in **seconds per km** (`double`/`int`).
- GPS noise filter (in `WorkoutSession`): reject accuracy > 25 m, steps < 3 m, speeds > 12 m/s, non-increasing timestamps.
- User-visible text goes in `res/values/strings.xml`.
- Never add a map SDK, analytics, ads, or network permission.
