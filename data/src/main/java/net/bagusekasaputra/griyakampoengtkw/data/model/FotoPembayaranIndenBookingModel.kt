package net.bagusekasaputra.griyakampoengtkw.data.model

import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.FotoPembayaranIndenBooking
import java.io.File

data class FotoPembayaranIndenBookingModel(
    val keyId: String = "",
    val termin: String = "",
    val uriStr: String = "",
) {

    companion object {
        fun getFilename(keyId: String, termin: String): String {
            return FotoPembayaranIndenBooking(keyId, termin, "").getFilename()
        }

        fun getFolderPath(rootExternalFile: File?): File {
            val folderPath = FotoPembayaranIndenBooking("", "", "").getFolderPath()
            return File(rootExternalFile, folderPath)
        }
    }
}