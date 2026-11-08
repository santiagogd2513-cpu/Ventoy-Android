package dev.ventoy.android

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Iso9660HeaderTest {
    @Test
    fun recognizesPrimaryVolumeDescriptor() {
        val descriptor = byteArrayOf(1) + "CD001".toByteArray(Charsets.US_ASCII) + byteArrayOf(1)
        assertTrue(Iso9660Header.isPrimaryVolumeDescriptor(descriptor))
    }

    @Test
    fun rejectsOtherVolumeDescriptorTypes() {
        val descriptor = byteArrayOf(2) + "CD001".toByteArray(Charsets.US_ASCII) + byteArrayOf(1)
        assertFalse(Iso9660Header.isPrimaryVolumeDescriptor(descriptor))
    }

    @Test
    fun rejectsWrongIdentifierAndShortBuffers() {
        assertFalse(Iso9660Header.isPrimaryVolumeDescriptor("XXXXX".toByteArray()))
        assertFalse(Iso9660Header.isPrimaryVolumeDescriptor(byteArrayOf(1, 2, 3)))
    }
}
