package net.bagusekasaputra.griyakampoengtkw.domain.entity.images

import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import java.io.File

data class FotoPembayaranIndenBooking(
    val keyId: String,
    val termin: String,
    val uriStr: String, // Can't use direct uri because it can't be mocked for test.
) {
    val filename = getFilename(keyId, termin)

    fun moveToExternalStorage(rootExternalDir: File?): FotoPembayaranIndenBooking {
        val imagePickerResultUri = Uri.parse(uriStr)
        val srcFile = imagePickerResultUri.toFile()

        val dstFile = getUriSavePath(rootExternalDir, keyId, termin).toFile()

        srcFile.renameTo(dstFile)

        return copy(uriStr = dstFile.toUri().toString())
    }

    companion object {
        fun getFolderPath(rootExternalDir: File?): File {
            return File(
                rootExternalDir,
                "inden_booking_images/foto_pembayaran_images"
            )
        }

        fun getFilename(keyId: String, termin: String): String {
            val replaceTerminSpaceWithUnderscore = termin.replace(" ", "_")
            return "$replaceTerminSpaceWithUnderscore--${keyId}.png"
        }

        fun getUriSavePath(rootExternalDir: File?, keyId: String, termin: String): Uri {
            val dstFolderPath = getFolderPath(rootExternalDir)
            if (!dstFolderPath.exists()) {
                dstFolderPath.mkdirs()
            }
            return File(dstFolderPath, getFilename(keyId, termin)).toUri()
        }
    }
}