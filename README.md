# Ventoy Android

An Android companion project exploring how to prepare Ventoy-compatible multiboot USB drives from Android devices.

> **Status:** Phase 1 — code creation in progress. This repository is an independent Android application project; it does not claim that the upstream Ventoy boot engine has already been ported.

## Project goals

- Native Android interface built with Kotlin and Jetpack Compose.
- Consistent dark appearance.
- USB host / OTG device discovery using Android APIs.
- ISO selection through Android's Storage Access Framework.
- A clearly separated native integration layer for future Ventoy components.
- Safe-by-default device handling: no automatic formatting or partition writes.

## Development phases

1. **Code creation:** application foundation, UI, device discovery, ISO catalog, architecture and CI configuration.
2. **Adaptation and implementation:** inspect and adapt upstream Ventoy components, implement supported USB preparation operations, test on real devices, and complete release validation.

## Important limitations

Android applications do not receive unrestricted raw block-device access by default. USB discovery and file access do not imply that partition-table modification or bootloader installation is possible on every device. Those operations require separate capability checks and explicit user confirmation. Root access, where considered, must be optional and clearly disclosed.

The app must never silently select, erase, repartition, or format a storage device.

## Build

Open this project in a recent Android Studio version with JDK 17 and Android SDK 35 installed. The GitHub Actions workflow builds the release APK when a version tag matching `v*` is pushed.

## Upstream project

Ventoy: https://github.com/ventoy/Ventoy

This project is not an official Ventoy release. Upstream code and bundled components must retain their applicable licenses and notices.
