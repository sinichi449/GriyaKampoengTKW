package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil

data class Pembayaran(
    val termin: String,
    val tanggal: String,
    val jumlahUangDibayar: String = "",
    var totalUangMasuk: String = "",
    var presentase: Double = 0.0,
    var sisaBelumTerbayar: String = "",
    val keterangan: String,
    // Timemillis is used as a Primary Key in the Room Database
    val timeMillis: Long,
    var sudahIsiFotoPembayaran: Boolean = false,
) {

    fun getUrutan(): Int {
        return termin.split(" ")[1].toInt()
    }

    fun getJenisTermin(): String {
        return termin.split(" ")[0]
    }

    companion object {
        fun hitungTotalUangMasuk(listPembayaran: List<Pembayaran>): Long {
            var mTotal = 0L

            listPembayaran.forEach {
                mTotal += NumberUtil.formatStringToLong(it.jumlahUangDibayar)
            }

            return mTotal
        }
    }
}