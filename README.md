# Ventoy Android

An Android companion project exploring how to prepare Ventoy-compatible multiboot USB drives from Android devices.

> **Status:** Phase 1 — code foundation complete. Phase 2 — first half in progress: upstream architecture audit, read-only ISO inspection, and USB descriptor inspection. The upstream Ventoy boot engine is not yet integrated.

## Project goals

- Native Android interface built with Kotlin and Jetpack Compose.
- Permanent dark appearance.
- USB host / OTG device discovery and conservative interface inspection.
- ISO document selection and read-only ISO-9660 descriptor validation through Android's Storage Access Framework.
- A narrow native integration boundary for future Ventoy components.
- Safe-by-default device handling: no automatic formatting or partition writes.

## Development phases

1. **Code creation — complete:** Android app foundation, dark UI, USB discovery, ISO picker, JNI/CMake placeholder, documentation, and CI/release workflows.
2. **Adaptation and implementation — in progress:** upstream Ventoy audit and initial Android-specific read-only adapters are underway. The remaining work is to evaluate a supported storage access path, adapt the relevant engine components, implement and verify a complete workflow, test on physical devices, and prepare the final release.

## Current capabilities

- Lists USB devices exposed by Android's USB Host API.
- Inspects USB interface descriptors and identifies interfaces declaring the USB Mass Storage class.
- Opens Android's document picker and retains read access to a selected ISO where the provider supports persistable grants.
- Performs a read-only ISO-9660 primary volume descriptor probe at the standard descriptor offset.
- Builds a native JNI/CMake placeholder library.
- Uses a permanent dark Material 3 color scheme.
- CI runs ISO header unit tests and compiles debug and release APK variants.

USB class detection does not grant raw disk access. ISO-9660 recognition does not prove that an image is bootable. The JNI library remains a status stub, not the Ventoy engine. The app does not yet install Ventoy or prepare a multiboot USB.

## Android storage limitations and safety

Android applications do not receive unrestricted raw block-device access by default. USB discovery and file access do not imply that partition-table modification or bootloader installation is possible on every device. A real installer must establish a supported storage path on the specific Android device before any write operation is designed. A no-root workflow remains the preferred target but is not guaranteed.

The app must never silently select, erase, repartition, format, or write to a storage device. Any future destructive operation must identify the exact target and require explicit confirmation.

## Upstream audit and licensing

The initial upstream architecture and compatibility findings are recorded in [docs/UPSTREAM_AUDIT.md](docs/UPSTREAM_AUDIT.md). Ventoy upstream: https://github.com/ventoy/Ventoy.

This project is not an official Ventoy release. No upstream installer or boot payload has been copied into the Android application yet. Before importing or adapting upstream code, preserve GPLv3 obligations, copyright notices, corresponding-source requirements, and component-specific licenses as described in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## Build

Open this project in Android Studio with JDK 17 and Android SDK 35 installed. GitHub Actions uses Gradle 8.9, Android SDK 35, CMake 3.22.1 and NDK 27.2.12479018.

## Continuous integration and releases

- Every push to `android-port` runs unit tests and compiles debug and release variants.
- Successful CI builds upload debug and release APKs as workflow artifacts.
- Pushing a version tag such as `v0.1.0-alpha1` runs the release workflow and publishes build outputs to GitHub Releases.
- The debug APK is installable for testing and uses the standard Android debug signing configuration.
- The release variant is currently unsigned. A stable, dedicated signing key must be configured securely in GitHub Actions before production distribution.
