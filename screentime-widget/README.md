# Screen Time Widget

Home screen widget that displays today's screen time with a modern gradient look.

## Build

1. Install Android SDK (API 34) and set `sdk.dir` in `local.properties`.
2. Use Gradle wrapper:

```bash
cd screentime-widget
./gradlew assembleDebug
```

## Usage access permission

To show screen time, the app needs Usage Access. Open the app and tap Grant permission, or go to Settings > Security & privacy > More security & privacy > Usage access, then enable for this app.

## Widget

Add the "Screen time" widget to your home screen. Tap the refresh icon to update, or tap the card to open the app. The widget auto-updates periodically.