package net.bagusekasaputra.griyakampoengtkw.data.model

import android.net.Uri
import android.os.Environment
import net.bagusekasaputra.griyakampoengtkw.domain.getFotoPembayaranFolderName
import java.io.File

data class FotoPembayaranModel(
    val id: Long? = null,
    val kavlingKode: String = "",
    val termin: String = "",
    val uriStr: String = "",
) {

    companion object {
        const val DST_FOLDER = "foto_pembayaran_images"

        /**
         * We need to create a folder structure like this:
         * - foto_pembayaran_images
         *  |- <kavling_kode>
         *     |- <kavling_kode>_<termin>.png
         */
        fun createKavlingFolderIfNotExist(externalFilesDir: File?, kavlingKode: String) {
            File(externalFilesDir, DST_FOLDER).let { rootDir ->
                File(rootDir, kavlingKode).let { targetDir ->
                    if (targetDir.exists().not()) targetDir.mkdir()
                }
            }
        }
    }

    fun getUri() = Uri.parse(uriStr)

    fun getFile(externalFilesDir: File?): File {
        val fileName = "${kavlingKode}_${termin}"
        val filePath = StringBuilder()
            .append(Environment.DIRECTORY_PICTURES).append("/")
            .append(getFotoPembayaranFolderName()).append("/")
            .append(fileName)
            .toString()

        return File(externalFilesDir, filePath)
    }

    fun getFilename() = "${kavlingKode}_${termin}.png"

    fun getKavlingAndFilePath() = "${kavlingKode}/${getFilename()}"
}