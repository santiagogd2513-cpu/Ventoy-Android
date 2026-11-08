package dev.ventoy.android

import android.content.ContentResolver
import android.net.Uri
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream

/**
 * Read-only ISO-9660 primary volume descriptor probe.
 *
 * SAF URIs are streams, not necessarily filesystem paths. This reader does not
 * copy, mount, modify, or execute the selected document.
 */
object IsoInspector {
    private const val DESCRIPTOR_OFFSET = 32 * 1024L
    private const val DESCRIPTOR_LENGTH = 7

    fun inspect(contentResolver: ContentResolver, uri: Uri): IsoInspection {
        val input = contentResolver.openInputStream(uri)
            ?: throw IOException("Android no pudo abrir el documento seleccionado.")

        input.use { stream ->
            val buffered = if (stream is BufferedInputStream) stream else BufferedInputStream(stream)
            skipFully(buffered, DESCRIPTOR_OFFSET)
            val descriptor = ByteArray(DESCRIPTOR_LENGTH)
            readFully(buffered, descriptor)

            if (!Iso9660Header.isPrimaryVolumeDescriptor(descriptor)) {
                return IsoInspection(
                    isIso9660 = false,
                    detail = "No se encontró un descriptor ISO-9660 válido en el sector esperado."
                )
            }
            return IsoInspection(
                isIso9660 = true,
                detail = "Se reconoció un descriptor primario ISO-9660. Esto no garantiza que la imagen sea arrancable."
            )
        }
    }

    private fun skipFully(input: InputStream, count: Long) {
        var remaining = count
        while (remaining > 0) {
            val skipped = input.skip(remaining)
            if (skipped > 0) {
                remaining -= skipped
            } else {
                if (input.read() == -1) throw IOException("El archivo es demasiado corto para ser una ISO.")
                remaining--
            }
        }
    }

    private fun readFully(input: InputStream, buffer: ByteArray) {
        var offset = 0
        while (offset < buffer.size) {
            val count = input.read(buffer, offset, buffer.size - offset)
            if (count < 0) throw IOException("La imagen terminó antes de completar el descriptor ISO.")
            offset += count
        }
    }
}

internal data class IsoInspection(
    val isIso9660: Boolean,
    val detail: String
)

internal object Iso9660Header {
    private val identifier = "CD001".toByteArray(Charsets.US_ASCII)

    fun isPrimaryVolumeDescriptor(bytes: ByteArray): Boolean =
        bytes.size >= 7 &&
            bytes[0].toInt() == 1 &&
            bytes.copyOfRange(1, 6).contentEquals(identifier) &&
            bytes[6].toInt() == 1
}
