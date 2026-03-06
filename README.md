# NomRide

[![License](https://img.shields.io/badge/License-MIT-0d1117?style=flat-square&logo=opensourceinitiative&logoColor=white)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Karoo%202%2F3-0d1117?style=flat-square&logo=android&logoColor=white)](https://www.hammerhead.io/)
[![Downloads](https://img.shields.io/github/downloads/yrkan/nomride/total?style=flat-square&color=0d1117&logo=github&logoColor=white)](https://github.com/yrkan/nomride/releases)
[![Release](https://img.shields.io/github/v/release/yrkan/nomride?style=flat-square&color=0d1117&logo=github&logoColor=white)](https://github.com/yrkan/nomride/releases/latest)

Real-time nutrition tracking extension for [Hammerhead Karoo](https://www.hammerhead.io/).

Track carb balance, burn rate, hydration, and get eat/drink reminders during rides — all directly on your Karoo screen.

## Features

- **Carb Balance** — real-time balance of carbs eaten vs burned, based on power/FTP zones
- **Burn Rate** — current carbohydrate burn rate (g/h)
- **Hydration Tracking** — water intake logging with rate (ml/h) and sip count
- **Quick Logging** — log food and water via bonus actions or tap
- **Eat/Drink Alerts** — configurable reminders based on time intervals
- **FIT Export** — carb data written to FIT files for post-ride analysis
- **Food Templates** — pre-configured food items (gels, bars, bananas, etc.)

## Data Fields

NomRide provides 6 data fields for your Karoo ride screens. Three of them (Carb Balance, Quick Log, Hydration) have custom graphical views that adapt to 6 layout sizes depending on how you arrange fields on your screen.

### Layout Sizes

| Size | Grid | Resolution | Content |
|------|------|-----------|---------|
| SMALL | Half-width, short | 235×195px | Value only |
| SMALL_WIDE | Full-width, short | 470×130-156px | Label + value |
| MEDIUM_WIDE | Full-width, medium | 470×195px | Label + value + metrics row |
| MEDIUM | Half-width, tall | 235×390px | Label + value + secondary info |
| LARGE | Full-width, tall | 470×250+px | Full detail with dividers and metric rows |
| NARROW | Half-width, very tall | 235×600+px | Stacked vertical layout |

### Graphical Fields

#### Carb Balance
Color-coded background based on balance level (green → dark green → yellow → orange → red). Shows balance value, burned/eaten totals, burn rate, and time since last food depending on available space.

| Balance | Background | Text |
|---------|-----------|------|
| > 0g | Green (#22C55E) | Black |
| 0 to -50g | Dark Green (#16A34A) | White |
| -50 to -100g | Yellow (#EAB308) | Black |
| -100 to -150g | Orange (#F97316) | White |
| < -150g | Red (#EF4444) | White |

#### Quick Log Status
Black background. Shows last logged food name, time since last intake (color-coded: green < 20min, amber < 40min, red > 40min), current balance, and burn rate.

#### Hydration
Black background with blue (#3B82F6) value text. Shows total water intake, rate (ml/h calculated from first water entry), and sip count.

### Numeric Fields

| Field | Description |
|-------|-------------|
| Carbs Burned | Total carbs burned during ride (g) |
| Carbs Eaten | Total carbs consumed during ride (g) |
| Burn Rate | Current burn rate (g/h) |

## Bonus Actions

- **Log Food** — open food logging screen
- **Log Water** — log water intake
- **Quick Gel** — instant-log default food template
- **Undo Last** — undo last intake entry (within 60s)

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
JAVA_HOME="/path/to/jdk" ANDROID_HOME="/path/to/android-sdk" ./gradlew app:assembleDebug
```

APK output: `app/build/outputs/apk/debug/nomride.apk`

### Requirements

- JDK 21 (Android Studio JBR recommended)
- Android SDK with compileSdk 35
- Gradle 8.7
- Kotlin 2.0.0

## Configuration

Tap the extension in Karoo's extension list to open settings:

- **FTP / Weight** — override Karoo profile values or use defaults
- **Food Templates** — customize quick-log food items (name, carbs per serving)
- **Eat Reminder Interval** — how often to remind you to eat (15/20/30/45/60 min)
- **Drink Reminder Interval** — how often to remind you to drink
- **Sound Alerts** — enable/disable audible reminders
- **FIT Export** — toggle carb data in FIT files (carbs_burned, carbs_eaten, carb_balance as DeveloperFields)

## How It Works

NomRide estimates carbohydrate burn rate from your real-time power output relative to FTP zones. Higher intensity = higher carb burn fraction. The carb balance shows eaten minus burned, helping you stay fueled during long rides.

### Architecture

- **CarbBalanceTracker** — core state engine with `StateFlow<RideNutritionState>`, manages burn calculations from 5-minute rolling window, intake logging, water tracking, and auto-save every 30s
- **6 DataTypes** — each extends `DataTypeImpl` from karoo-ext SDK, streaming values and rendering Glance views
- **Glance Components** — reusable composables (`DataFieldContainer`, `ColoredFieldContainer`, `ValueText`, `LabelText`, `MetricValueRow`, `DualMetric`, `TripleMetric`) with centralized `GlanceColors`
- **FIT Integration** — `DeveloperField` entries (Float32) written during Recording/Paused ride states

## License

MIT
