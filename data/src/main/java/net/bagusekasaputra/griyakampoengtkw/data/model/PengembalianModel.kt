package net.bagusekasaputra.griyakampoengtkw.data.model

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil

data class PengembalianModel(
    val keyId: String = "",
    val kavling: String = "",
    val namaCustomer: String = "",
    val tanggal: String = "",
    val jumlah: Long = 0L,
    val keterangan: String = "",
    val uri: String = "",
    val timeMillis: Long = 0L,
) {
    companion object {
        const val DST_DIR = "pengembalian_images"

        fun getFilename(keyId: String): String {
            return "${keyId}.png"
        }

        fun String.parseJumlahUang(): Long {
            return NumberUtil.formatStringToLong(this)
        }

        fun Long.parseJumlahUang(): String {
            return NumberUtil.formatLongToString(this)
        }
    }
}