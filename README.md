# NomRide

Real-time nutrition tracking extension for [Hammerhead Karoo](https://www.hammerhead.io/).

Track carb balance, burn rate, hydration, and get eat/drink reminders during rides — all directly on your Karoo screen.

## Features

- **Carb Balance** — real-time balance of carbs eaten vs burned, based on power/FTP zones
- **Burn Rate** — current carbohydrate burn rate (g/h)
- **Hydration Tracking** — water intake logging with drink reminders
- **Quick Logging** — log food and water via bonus actions or tap
- **Eat/Drink Alerts** — configurable reminders based on time intervals
- **FIT Export** — carb data written to FIT files for post-ride analysis
- **Food Templates** — pre-configured food items (gels, bars, bananas, etc.)

## Data Fields

| Field | Type | Description |
|-------|------|-------------|
| Carb Balance | Graphical | Eaten minus burned (g) with color zones |
| Carbs Burned | Numeric | Total carbs burned during ride (g) |
| Carbs Eaten | Numeric | Total carbs consumed during ride (g) |
| Burn Rate | Numeric | Current burn rate (g/h) |
| Quick Log | Graphical | Last logged item + time since last intake |
| Hydration | Graphical | Water intake tracking |

## Bonus Actions

- **Log Food** — open food logging screen
- **Log Water** — log water intake
- **Quick Gel** — instant-log default food template
- **Undo Last** — undo last intake entry

## Installation

### Sideload

1. Download `nomride.apk` from [Releases](https://github.com/yrkan/nomride/releases/latest)
2. Install via ADB:
   ```
   adb install nomride.apk
   ```
3. The extension appears automatically in Karoo's extension list

## Build from Source

```bash
git clone https://github.com/yrkan/nomride.git
cd nomride
JAVA_HOME="/path/to/jdk" ANDROID_HOME="/path/to/android-sdk" ./gradlew app:assembleRelease
```

APK output: `app/build/outputs/apk/release/nomride.apk`

## Configuration

Tap the extension in Karoo's extension list to open settings:

- **FTP / Weight** — override Karoo profile values or use defaults
- **Food Templates** — customize quick-log food items
- **Eat Reminder Interval** — how often to remind you to eat
- **Drink Reminder Interval** — how often to remind you to drink
- **Sound Alerts** — enable/disable audible reminders
- **FIT Export** — toggle carb data in FIT files

## How It Works

NomRide estimates carbohydrate burn rate from your real-time power output relative to FTP zones. Higher intensity = higher carb burn fraction. The carb balance shows eaten minus burned, helping you stay fueled during long rides.

## License

MIT
