package dev.ventoy.android

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UsbInspectionTest {
    @Test
    fun recognizesUsbMassStorageClass() {
        assertTrue(UsbInspection.isMassStorageInterfaceClass(0x08))
    }

    @Test
    fun rejectsNonStorageUsbClasses() {
        assertFalse(UsbInspection.isMassStorageInterfaceClass(0x03))
        assertFalse(UsbInspection.isMassStorageInterfaceClass(0xFF))
    }
}
