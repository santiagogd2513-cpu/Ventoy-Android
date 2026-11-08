# Upstream Ventoy audit — Android port

Audit baseline: upstream repository `ventoy/Ventoy`, commit `74cff34240dca6991b79c6656770670da7b0c39e` (the source revision inspected for this phase).

## What the upstream installer actually does

The upstream Linux installer is not a portable Android library. Its documented command-line interface targets a Linux block device such as `/dev/sdb`, and supports install, force-install, update, MBR/GPT selection, secure-boot configuration, and reserved disk space.

The inspected installer package expects a prepared directory tree containing boot images, GRUB assets, architecture-specific tools, and shell utilities. The shell library relies on Linux utilities and host facilities such as partitioning tools, filesystem formatters, device nodes, and direct disk operations. The Linux GUI implementation also includes Linux-specific headers and system calls (for example block-device ioctls and mount-related interfaces).

The upstream layout includes at least these distinct layers:

- **Installer / packaging:** Linux shell scripts and platform-specific helper binaries.
- **Disk and partition logic:** Linux-specific utilities for MBR/GPT structures, partition sizing, and filesystem setup.
- **Boot payload:** boot images, EFI binaries, GRUB and Ventoy-specific GRUB modules/configuration.
- **Runtime support:** filesystem and image-handling components used after the USB has been prepared.

These pieces cannot be made Android-compatible merely by compiling the existing Linux installer source with the Android NDK. The host-side disk API and process/tool assumptions must be replaced or isolated.

## Android feasibility findings

- Android USB Host exposes USB devices, interfaces, endpoints and permission-controlled USB transfers.
- USB Host discovery does **not** expose a generic `/dev/sdX` block device to an ordinary app and does not provide raw partition-table access.
- Selecting an ISO with the Storage Access Framework provides a document URI, not a local POSIX path. Future ISO readers must use `ContentResolver` streams or file descriptors and handle providers that are not seekable.
- A USB Mass Storage interface descriptor is evidence of a USB interface class, not proof that the app can safely issue arbitrary block writes or prepare the device.
- A no-root installation workflow remains the preferred goal, but is not assumed possible until a supported Android-specific storage path is demonstrated on target hardware.

## Port strategy selected

1. Keep the Android UI and document-provider boundary in Kotlin.
2. Implement read-only ISO inspection through SAF before considering any install path.
3. Inspect USB interface descriptors and report capabilities conservatively.
4. Keep native code behind a narrow JNI boundary; do not import Linux-only installer code into Android unchanged.
5. Separate platform-independent format/metadata code from Linux-specific device I/O.
6. Do not implement destructive operations until the target-storage access model, device identity checks, confirmation flow, and verification strategy are established.

## License and provenance

The inspected upstream repository contains a GPL-3.0 license in `COPYING`. The Ventoy installer and Linux utility sources inspected include GPL-3.0-or-later notices. GRUB and other bundled components may carry their own copyright and license obligations. Before distributing adapted upstream code or binaries, the project must preserve each applicable notice, provide corresponding source as required, and audit every imported component separately.

No upstream installer or boot payload has been copied into the Android application by this audit. This document records architecture and compatibility findings; it is not a claim that the Ventoy engine has already been ported.
