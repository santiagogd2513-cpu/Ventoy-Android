# Phase 2 — implementation status

## Delivered in this repository

- Audited upstream Ventoy's Linux installer, disk utilities, boot payload layout, and licensing baseline.
- Added a Kotlin ISO-9660 primary-volume-descriptor probe through Android's Storage Access Framework. It is read-only and deliberately does not claim that an ISO is bootable.
- Added USB descriptor inspection, including Mass Storage class recognition and bulk endpoint classification.
- Added JUnit coverage for valid/invalid ISO descriptor headers and USB Mass Storage class recognition.
- Kept the native JNI/CMake library explicitly identified as a placeholder rather than misrepresenting it as the Ventoy engine.
- Configured CI to run unit tests and build debug/release APK variants, then publish artifacts. The release workflow is present; release signing still needs a dedicated key.

## Not implemented — do not treat this repository as a Ventoy installer

- USB permission flow and a tested USB Mass Storage Bulk-Only Transport driver.
- SCSI inquiry, capacity, read/write command implementation.
- Partition table creation/update, filesystem creation, Ventoy metadata, EFI/GRUB payload installation, and boot verification.
- Import or adaptation of the upstream Ventoy engine and its boot assets.
- Physical-device compatibility tests, including Android vendor variations and USB OTG hubs.
- Signed production release.

## Why the remaining work is not a safe “copy and compile”

The Linux installer relies on Linux block-device paths and host utilities. Android USB Host does not expose a generic block node, but it can permit direct USB transfers to compatible devices. A no-root storage adapter may therefore be possible by implementing USB Mass Storage Bulk-Only Transport and SCSI over the device's bulk endpoints. That route has not yet been implemented or validated here, so it must not be presented as working.

Before writing support is added, the app must first demonstrate read-only device identification and capacity reads on physical Android hardware. Any future writer must require the user to select the exact device, display its identity and capacity, ask for a separate explicit confirmation, abort on identity changes or transport errors, and verify written data by reading it back.

## Release decision

The Phase 2 Android feasibility groundwork is committed. The requested end-to-end Ventoy USB installer is **not complete** and must not be released or described as functional. A successful Android APK build would only validate the app scaffold and current read-only features, not Ventoy installation capability.
