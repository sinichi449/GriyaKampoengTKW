package net.bagusekasaputra.griyakampoengtkw.data.model

import android.net.Uri
import android.os.Environment
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import java.io.File

data class FotoPembayaranModel(
    val id: Long? = null,
    val kavlingKode: String = "",
    val termin: String = "",
    val uriStr: String = "",
) {

    fun getUri() = Uri.parse(uriStr)

    fun getFile(externalFilesDir: File?): File {
        val fileName = "${kavlingKode}_${termin}"
        val filePath = StringBuilder()
            .append(Environment.DIRECTORY_PICTURES).append("/")
            .append(GriyaNodes.fotoPembayaranFilePath).append("/")
            .append(fileName)
            .toString()

        return File(externalFilesDir, filePath)
    }
}