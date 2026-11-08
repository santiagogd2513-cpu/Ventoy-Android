# Android port architecture

## Current application boundary

- **UI:** Kotlin + Jetpack Compose. The initial design uses a permanent dark Material 3 color scheme.
- **USB discovery:** Android USB Host API through `UsbManager`. Discovery is informational only and does not imply raw disk access.
- **ISO selection:** Android Storage Access Framework. The current implementation selects a document URI; it does not yet validate, copy, mount, parse, or write the image.
- **Native boundary:** a small JNI/CMake shared library proves the Android NDK integration path. It is a status stub, not the Ventoy boot engine.
- **Build:** GitHub Actions compiles debug and release APK variants using JDK 17, Gradle 8.9, Android SDK 35, CMake 3.22.1 and NDK 27.2.12479018.

## Phase 1 completion boundary

Phase 1 supplies a buildable Android shell and automation. It does not claim to implement Ventoy's bootloader, partition layout, ISO processing, or USB installation. No upstream Ventoy source or binary has been copied into the application yet.

The CI build currently compiles native libraries for Android's default NDK ABI set (arm64-v8a, armeabi-v7a, x86 and x86_64). Successful compilation is not equivalent to runtime testing on devices with those architectures.

## Future integration constraints

The upstream Ventoy project contains boot-time components and host-side installation utilities. Android's public USB Host API exposes USB interfaces and endpoints; it does not grant arbitrary raw block-device access to a mass-storage LUN. A real installer must establish a supported, device-specific storage path before any partition or boot-sector operation can be implemented.

Do not treat a USB device being visible in `UsbManager` as proof that it can be safely or completely prepared by this application. A no-root workflow remains the preferred first target, but cannot be promised until the Android device APIs and storage path are proven.

## Safety invariants

1. Never choose a target disk automatically.
2. Never erase, repartition, format, or write without explicit target identification and confirmation.
3. Display the target's manufacturer/product identifiers and capacity when reliable information is available.
4. Abort if the target identity changes during an operation.
5. Do not claim success until read-back verification passes.
6. Keep root-dependent capabilities optional and clearly separated from the normal, unprivileged mode.

## ABI direction

All four default Android NDK ABIs currently compile in CI. Runtime validation on physical Android devices remains outstanding; ARM64 is the primary target for the first hardware validation.
