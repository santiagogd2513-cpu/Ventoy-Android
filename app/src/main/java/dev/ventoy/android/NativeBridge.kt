package dev.ventoy.android

/**
 * JNI boundary for future native Ventoy integration.
 *
 * This bridge is deliberately limited to a status probe in the initial app scaffold.
 * It does not access block devices, partition tables, or write to USB storage.
 */
object NativeBridge {
    init {
        System.loadLibrary("ventoy_android")
    }

    external fun getBridgeStatus(): String
}
