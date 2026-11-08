# Android port architecture

## Current application boundary

- **UI:** Kotlin + Jetpack Compose with a permanent dark Material 3 color scheme.
- **USB discovery:** Android USB Host API through `UsbManager`. The app lists device IDs, vendor/product IDs, and interface descriptors. It can identify an interface declaring USB Mass Storage class 0x08; this is not raw block-device access.
- **ISO selection:** Android Storage Access Framework. The app retains a read grant where supported and probes the ISO-9660 primary volume descriptor at byte offset 32768. This is read-only and does not establish bootability.
- **Native boundary:** a small JNI/CMake shared library proves the Android NDK integration path. It remains a status stub, not the Ventoy boot engine.
- **Tests:** JUnit unit tests cover valid and invalid ISO-9660 primary-volume-descriptor headers.
- **Build:** GitHub Actions runs unit tests and compiles debug and release APK variants with JDK 17, Gradle 8.9, Android SDK 35, CMake 3.22.1 and NDK 27.2.12479018.

## Upstream compatibility audit

See [UPSTREAM_AUDIT.md](UPSTREAM_AUDIT.md) for the source revision and findings. The upstream Linux installer expects Linux device nodes, host utilities, partitioning/filesystem tools, and direct block-device operations. GRUB and Ventoy boot payloads form a separate boot-time layer. The Linux GUI utility uses Linux-specific system interfaces and cannot be treated as an Android library without replacing its host integration.

## Remaining Android integration constraints

Android's public USB Host API exposes USB interfaces and endpoints; it does not grant arbitrary raw block-device access to a mass-storage LUN. A real installer must establish a supported, device-specific storage path before any partition or boot-sector operation can be implemented.

A SAF URI is not necessarily a local filesystem path or seekable file. ISO readers must use ContentResolver streams/file descriptors and handle providers that cannot seek. The current probe is read-only and only checks one ISO-9660 metadata signature.

A no-root workflow remains the preferred first target, but cannot be promised until a supported path is demonstrated on target hardware.

## Safety invariants

1. Never choose a target disk automatically.
2. Never erase, repartition, format, or write without explicit target identification and confirmation.
3. Display the target's manufacturer/product identifiers and capacity when reliable information is available.
4. Abort if the target identity changes during an operation.
5. Do not claim success until read-back verification passes.
6. Keep root-dependent capabilities optional and clearly separated from the normal, unprivileged mode.

## Native integration direction

Keep Android UI, SAF, and USB Host handling in Kotlin. Only move portable format or metadata code behind JNI after auditing dependencies and licenses. Linux-specific device I/O must be replaced by a separately validated Android storage adapter; do not compile Linux installer code unchanged with the NDK.

All four default Android NDK ABIs currently compile in CI. Runtime validation on physical Android devices remains outstanding; ARM64 is the primary target for the first hardware validation.
