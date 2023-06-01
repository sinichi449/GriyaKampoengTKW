package net.bagusekasaputra.griyakampoengtkw.data.model

import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiriIndenBooking
import java.io.File

data class ImageDataDiriIndenBookingModel(
    val keyId: String = "",
    val uriStr: String = "",
) {

    companion object {
        fun getFilename(keyId: String): String {
            return ImageDataDiriIndenBooking(keyId, "").getFilename()
        }

        fun getFolderPath(rootExternalDir: File?): File {
            val folderPath = ImageDataDiriIndenBooking("", "").getFolderPath()

            return File(rootExternalDir, folderPath)
        }
    }
}