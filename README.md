# 🐍 PySnake — Python Coding Practice & Arcade Break

A modern Android application built with **Kotlin** and **Jetpack Compose** that combines interactive Python coding exercises with an integrated retro arcade Snake game break feature.

---

## 🚀 Features

- **Python Exercise Module:**
  - 18 curated challenges across Beginner, Intermediate, and Advanced difficulties.
  - Interactive code editor with line numbers, real-time syntax highlighting, and a quick-symbol bar.
  - Built-in embedded Python execution engine comparing outputs against test cases.
  - Persistent progress, streaks, and XP tracking powered by Room Database.

- **Arcade Snake Break Zone:**
  - 20-minute study goal countdown with break recommendations and instant triggers.
  - Smooth canvas physics, glowing snake segments, and animated food types (Apples, Golden Bugs, Bonus Stars).
  - Dual control options: Fluid on-screen swipe gestures + 4-way arcade D-Pad with haptic vibration.
  - Seamless "Return to Coding" / "Back to Lab" button to resume right where you left off.

---

## 📦 How to Download & Install the APK

1. Go to the **Releases** tab on this GitHub repository.
2. Under the latest release (e.g. `v1.0.0`), download `app-debug.apk`.
3. Open the downloaded `.apk` file on your Android device (ensure "Install from unknown sources" is enabled for your browser/file manager).

---

## 🛠️ Building from Source

```bash
# Clone the repository
git clone https://github.com/<your-username>/pysnake.git
cd pysnake

# Run unit tests
gradle :app:testDebugUnitTest

# Build debug APK
gradle :app:assembleDebug
```

The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.
