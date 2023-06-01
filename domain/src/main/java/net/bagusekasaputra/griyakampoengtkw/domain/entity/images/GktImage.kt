package net.bagusekasaputra.griyakampoengtkw.domain.entity.images

import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import java.io.File

abstract class GktImage(var uriStr: String) {

    abstract fun getFilename(): String

    abstract fun getFolderPath(): String

    fun getUriSavePath(rootExternalDir: File?): Uri {
        val dstFolderPath = File(rootExternalDir, getFolderPath())
        if (!dstFolderPath.exists()) {
            dstFolderPath.mkdirs()
        }

        return File(dstFolderPath, getFilename()).toUri()
    }

    fun moveToExternalStorage(rootExternalDir: File?) {
        if (uriStr.isEmpty()) {
            throw IllegalStateException("Uri is EMPTY!")
        }
        val imagePickerResultUri = Uri.parse(uriStr)
        val srcFile = imagePickerResultUri.toFile()

        val dstFile = getUriSavePath(rootExternalDir).toFile()

        srcFile.renameTo(dstFile)

        uriStr = srcFile.toUri().toString()
    }
}