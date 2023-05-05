package net.bagusekasaputra.griyakampoengtkw.data.model

import java.io.File

data class IndenBookingModel(
    val timeMillis: Long = 0L,
    val namaCostumer: String = "",
    val tanggalDibayar: String = "",
    var fotoPembayaranPath: String = "",
    val jumlahUang: Long = 0L,
    val noHp: String = "",
    val keterangan: String = "",
) {

    fun getFileName(): String {
        // set all to uppercase and replace spaces into underscore
        val mNamaCostumer = namaCostumer.uppercase().replace(" ", "_")
        val mTanggalDibayar = tanggalDibayar.replace("/", "_")

        return "${mNamaCostumer}-${mTanggalDibayar}.png"
    }

    fun getStorageFolderAndFileName(): String {
        return "$DST_FOLDER/${getFileName()}"
    }

    companion object {
        const val DST_FOLDER = "inden_booking_images"

        fun createStorageFolderIfNotExist(externalFilesDir: File?) {
            val targetDir = File(externalFilesDir, DST_FOLDER)
            if (!targetDir.exists()) targetDir.mkdir()
        }
    }
}
