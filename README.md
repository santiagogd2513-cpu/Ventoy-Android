# Ventoy Android

An Android companion project exploring how to prepare Ventoy-compatible multiboot USB drives from Android devices.

> **Status:** Android feasibility prototype. The Phase 2 groundwork includes the upstream audit, read-only ISO inspection, USB descriptor inspection, and unit tests. The actual Ventoy disk-preparation engine and boot payload are not integrated; this repository is not yet a working Ventoy USB installer.

## Project goals

- Native Android interface built with Kotlin and Jetpack Compose.
- Permanent dark appearance.
- USB host / OTG device discovery and conservative interface inspection.
- ISO document selection and read-only ISO-9660 descriptor validation through Android's Storage Access Framework.
- A narrow native integration boundary for future Ventoy components.
- Safe-by-default device handling: no automatic formatting or partition writes.

## Development phases

1. **Code creation — complete:** Android app foundation, dark UI, USB discovery, ISO picker, JNI/CMake placeholder, documentation, and CI/release workflows.
2. **Adaptation and implementation — groundwork delivered, installer integration pending:** the upstream audit, Android-specific read-only adapters, ISO/USB unit tests, and CI artifact pipeline are present. Completing a real installer still requires a tested USB Mass Storage transport (for example, USB Bulk-Only Transport with SCSI commands), disk-layout and boot-payload integration, read-back verification, and physical-device testing. Those components are not represented as complete.

## Current capabilities

- Lists USB devices exposed by Android's USB Host API.
- Inspects USB interface descriptors and identifies interfaces declaring the USB Mass Storage class.
- Includes unit tests for USB Mass Storage class classification and ISO-9660 header recognition.
- Opens Android's document picker and retains read access to a selected ISO where the provider supports persistable grants.
- Performs a read-only ISO-9660 primary volume descriptor probe at the standard descriptor offset.
- Builds a native JNI/CMake placeholder library.
- Uses a permanent dark Material 3 color scheme.
- CI runs ISO header unit tests and compiles debug and release APK variants.

USB class detection alone does not grant disk access; a USB Mass Storage transport implementation and Android USB permission are still required. ISO-9660 recognition does not prove that an image is bootable. The JNI library remains a status stub, not the Ventoy engine. The app does not yet install Ventoy or prepare a multiboot USB.

## Android storage limitations and safety

Android applications do not receive a generic `/dev/sdX` block device through the public USB Host API. However, an app with USB-device permission can communicate with compatible USB Mass Storage interfaces through bulk endpoints and implement the Bulk-Only Transport / SCSI protocol itself. This project has not implemented or validated that transport yet. Device compatibility, interface claims, mounted-volume conflicts, command handling, capacity reporting, and failure recovery must be tested before any writer is added. A no-root workflow remains a goal, not a verified capability.

The app must never silently select, erase, repartition, format, or write to a storage device. Any future destructive operation must identify the exact target and require explicit confirmation.

## Upstream audit and licensing

The initial upstream architecture and compatibility findings are recorded in [docs/UPSTREAM_AUDIT.md](docs/UPSTREAM_AUDIT.md). Ventoy upstream: https://github.com/ventoy/Ventoy.

This project is not an official Ventoy release. No upstream installer or boot payload has been copied into the Android application yet. Before importing or adapting upstream code, preserve GPLv3 obligations, copyright notices, corresponding-source requirements, and component-specific licenses as described in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## Build

Open this project in Android Studio with JDK 17 and Android SDK 35 installed. The repository includes the Gradle 8.9 wrapper (`./gradlew` on Linux/macOS or `gradlew.bat` on Windows). GitHub Actions uses Android SDK 35, CMake 3.22.1 and NDK 27.2.12479018.

## Continuous integration and releases

- Every push to `android-port` runs unit tests and compiles debug and release variants.
- Successful CI builds upload debug and release APKs as workflow artifacts.
- Pushing a version tag such as `v0.1.0-alpha1` runs the release workflow and publishes build outputs to GitHub Releases.
- The debug APK is installable for testing and uses the standard Android debug signing configuration.
- The release variant is currently unsigned. A stable, dedicated signing key must be configured securely in GitHub Actions before production distribution.
