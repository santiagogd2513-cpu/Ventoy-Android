package dev.ventoy.android

import android.hardware.usb.UsbDevice

internal data class UsbInterfaceInspection(
    val interfaceId: Int,
    val interfaceClass: Int,
    val interfaceSubclass: Int,
    val interfaceProtocol: Int,
    val endpointCount: Int,
    val hasBulkIn: Boolean,
    val hasBulkOut: Boolean
)

internal data class UsbDeviceInspection(
    val interfaces: List<UsbInterfaceInspection>,
    val hasMassStorageInterface: Boolean
)

internal object UsbInspection {
    private const val USB_CLASS_MASS_STORAGE = 0x08
    private const val USB_ENDPOINT_XFER_MASK = 0x03
    private const val USB_ENDPOINT_XFER_BULK = 0x02
    private const val USB_DIR_IN = 0x80

    fun inspect(device: UsbDevice): UsbDeviceInspection {
        val interfaces = (0 until device.interfaceCount).map { index ->
            val usbInterface = device.getInterface(index)
            var hasBulkIn = false
            var hasBulkOut = false

            for (endpointIndex in 0 until usbInterface.endpointCount) {
                val endpoint = usbInterface.getEndpoint(endpointIndex)
                val isBulk = (endpoint.attributes and USB_ENDPOINT_XFER_MASK) == USB_ENDPOINT_XFER_BULK
                if (isBulk && (endpoint.direction and USB_DIR_IN) == USB_DIR_IN) hasBulkIn = true
                if (isBulk && (endpoint.direction and USB_DIR_IN) == 0) hasBulkOut = true
            }

            UsbInterfaceInspection(
                interfaceId = usbInterface.id,
                interfaceClass = usbInterface.interfaceClass,
                interfaceSubclass = usbInterface.interfaceSubclass,
                interfaceProtocol = usbInterface.interfaceProtocol,
                endpointCount = usbInterface.endpointCount,
                hasBulkIn = hasBulkIn,
                hasBulkOut = hasBulkOut
            )
        }

        return UsbDeviceInspection(
            interfaces = interfaces,
            hasMassStorageInterface = interfaces.any { it.interfaceClass == USB_CLASS_MASS_STORAGE }
        )
    }
}
