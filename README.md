# FCM Token Viewer (com.utrapp)

A minimal Android app that fetches and displays the Firebase Cloud Messaging (FCM) device token on launch. Includes a **Copy Token** button.

## How to build (Android Studio recommended)
1. Open this folder in **Android Studio** (Giraffe+).
2. Wait for Gradle sync (internet required).
3. From the menu: **Build → Build APK(s)**.
4. Install the generated APK on an emulator or device.

## Notes
- This project already includes your `app/google-services.json` (for package `com.utrapp`).
- On Android 13+, the app will request the **POST_NOTIFICATIONS** runtime permission.
- The token appears on screen and can be copied via the button.
