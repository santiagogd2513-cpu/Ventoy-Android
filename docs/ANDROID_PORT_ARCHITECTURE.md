# Android port architecture

## Current application boundary

- **UI:** Kotlin + Jetpack Compose. The initial design uses a dark Material 3 color scheme.
- **USB discovery:** Android USB Host API through `UsbManager`. Discovery is informational only.
- **ISO selection:** Android Storage Access Framework. The first implementation selects a document URI; it does not yet copy, mount, parse, or write the image.
- **Native boundary:** a small JNI/CMake shared library proves the Android NDK integration path. It is a status stub, not the Ventoy boot engine.

## Future integration constraints

The upstream Ventoy project contains boot-time components and host-side installation utilities. Android's public USB Host API exposes USB interfaces and endpoints; it does not grant arbitrary raw block-device access to a mass-storage LUN. A real installer must establish a supported, device-specific storage path before any partition or boot-sector operation can be implemented.

Do not treat a USB device being visible in `UsbManager` as proof that it can be safely or completely prepared by this application.

## Safety invariants

1. Never choose a target disk automatically.
2. Never erase, repartition, format, or write without explicit target identification and confirmation.
3. Display the target's manufacturer/product identifiers and capacity when reliable information is available.
4. Abort if the target identity changes during an operation.
5. Do not claim success until read-back verification passes.
6. Keep root-dependent capabilities optional and clearly separated from the normal, unprivileged mode.

## ABI direction

The first target is Android ARM64 (`arm64-v8a`). Other ABIs can be added after native dependencies and their licenses/build systems have been audited.
