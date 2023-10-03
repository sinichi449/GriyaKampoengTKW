package net.bagusekasaputra.griyakampoengtkw.data.model

import java.io.File

data class FotoTambahanPembayaranModel(
    val kavling: String = "",
    val tambahanPembayaranId: String = "",
    var uri: String = "",
) {
    companion object {
        const val DST_FOLDER = "tambahan_pembayaran_images"

        /**
         * We need to create a folder structure like this:
         * - tambahan_pembayaran_images
         *  |- <kavling_kode>
         *     |- <kavling_kode>_<tambahanPembayaranId>.png
         */
        fun createKavlingFolderIfNotExist(externalFilesDir: File?, kavling: String) {
            File(externalFilesDir, DST_FOLDER).let { rootDir ->
                File(rootDir, kavling).let { targetDir ->
                    if (!targetDir.exists()) targetDir.mkdirs()
                }
            }
        }
    }

    fun getFilename() = "${kavling}_${tambahanPembayaranId}.png"

    fun getKavlingAndFilePath() = "${kavling}/${getFilename()}"
}