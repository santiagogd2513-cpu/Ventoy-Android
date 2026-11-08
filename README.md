# Ventoy Android

An Android companion project exploring how to prepare Ventoy-compatible multiboot USB drives from Android devices.

> **Status:** Phase 1 — code creation complete. The Android application foundation and CI build are in place. This is not yet a functional port of the upstream Ventoy boot engine.

## Project goals

- Native Android interface built with Kotlin and Jetpack Compose.
- Consistent dark appearance.
- USB host / OTG device discovery using Android APIs.
- ISO file selection through Android's Storage Access Framework.
- A clearly separated native integration layer for future Ventoy components.
- Safe-by-default device handling: no automatic formatting or partition writes.

## Development phases

1. **Code creation — complete:** application foundation, dark UI, USB discovery, ISO document selection, JNI/CMake placeholder, architecture and CI/release workflows.
2. **Adaptation and implementation — not started:** inspect and adapt upstream Ventoy components, implement only technically supported USB preparation operations, test on real devices, and complete release validation.

## Current capabilities

- Displays USB devices exposed by Android's USB Host API.
- Opens Android's document picker and remembers the selected ISO document URI for the current app session.
- Builds a native JNI/CMake placeholder library.
- Uses a permanent dark Material 3 color scheme.
- GitHub Actions compiles debug and release APK variants.

These are foundation features only. The app does not yet validate or parse ISO images, install Ventoy, create a multiboot USB, or write to storage. The JNI library is a status stub, not the Ventoy engine.

## Android storage limitations and safety

Android applications do not receive unrestricted raw block-device access by default. USB discovery and file access do not imply that partition-table modification or bootloader installation is possible on every device. A real installer must establish a supported storage path on the specific Android device before any write operation is designed. Root access, if eventually needed, must remain optional where feasible and clearly disclosed.

The app must never silently select, erase, repartition, format, or write to a storage device. Any future destructive operation must identify the exact target and require explicit confirmation.

## Build

Open this project in a recent Android Studio version with JDK 17 and Android SDK 35 installed. GitHub Actions uses Gradle 8.9, Android SDK 35, CMake 3.22.1 and NDK 27.2.12479018.

## Upstream project and licensing

Ventoy: https://github.com/ventoy/Ventoy

This project is not an official Ventoy release. No upstream Ventoy source code or binary payload is included in Phase 1. Before importing or adapting upstream code, preserve GPLv3 obligations, copyright notices, and component-specific licenses as described in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## Continuous integration and releases

- Every push to `android-port` runs the Android CI workflow and compiles debug and release variants.
- Successful CI builds upload debug and release APKs as workflow artifacts.
- Pushing a version tag such as `v0.1.0-alpha1` runs the release workflow and publishes build outputs to GitHub Releases.
- The debug APK is installable for testing and uses the standard Android debug signing configuration.
- The release variant is currently unsigned. A stable, dedicated signing key must be configured securely in GitHub Actions before production distribution.
