package net.bagusekasaputra.griyakampoengtkw.data.model

import java.io.File

data class FotoTambahLuasanModel(
    val tambahLuasanId: String = "",
    val kavling: String,
    val uri: String,
) {

    companion object {
        const val DST_FOLDER = "tambah_luasan_images"

        fun createKavlingFolderIfNotExists(externalFilesDir: File?, kavling: String) {
            File(externalFilesDir, DST_FOLDER).also { root ->
                File(root, kavling).also { targetDir ->
                    if (!targetDir.exists()) targetDir.mkdirs()
                }
            }
        }
    }

    fun getFilename() = "$tambahLuasanId.png"
    fun getKavlingAndFilePath() = "${kavling}/${getFilename()}"

}