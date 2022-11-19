package net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoPembayaran.device

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toFile
import androidx.core.net.toUri
import java.io.File

object ImageUtil {

    fun copyImageAndGetUri(externalFileDir: File?, srcUri: Uri, fileName: String): Uri {
        val src = srcUri.toFile()
        val pictureDirectory = File(externalFileDir, Environment.DIRECTORY_PICTURES)
        val dst = File(pictureDirectory, fileName)

        src.copyTo(dst, true)

        return dst.toUri()
    }

    fun deleteImagePickerLeftOver(externalFileDir: File?) {
        val imagePickerDirectory = File(externalFileDir, Environment.DIRECTORY_DCIM)
        imagePickerDirectory.listFiles()?.forEach { it?.delete() }

        imagePickerDirectory.delete()
    }

    fun getBitmapFromUri(contentResolver: ContentResolver,uri: Uri): Bitmap {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)

        parcelFileDescriptor?.close()

        return image
    }

}