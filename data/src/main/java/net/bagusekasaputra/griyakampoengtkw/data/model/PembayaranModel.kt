package net.bagusekasaputra.griyakampoengtkw.data.model

import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran

data class PembayaranModel(
    val termin: String = "",
    val urutan: Int = 0,
    val tanggal: String = "",
    val jumlahUangDibayar: Long = 0L,
    val keterangan: String = "",
    val timeMillis: Long = 0L,
    val invoiceDateStr: String = "",
) {
    fun getFullTermin()
        = "$termin $urutan"

    companion object {
        const val KEY_JENIS_TERMIN = "jenis"
        const val KEY_URUTAN_TERMIN = "urutan"
        const val INVOICE_SEPARATOR = "/"

        fun pisahkanTerminDanUrutan(termin: String): Map<String, String> {
            val terminDanUrutan = termin.split(" ")
            return mapOf(
                Pair(KEY_JENIS_TERMIN, terminDanUrutan[0]),
                Pair(KEY_URUTAN_TERMIN, terminDanUrutan[1]),
            )
        }

        fun BulanAngsuran.toInvoiceDateStr(): String {
            val bulanInvoice = bulan.toString().padStart(2, '0')
            val tahunInvoice = tahun.toString()

            return "${bulanInvoice}${INVOICE_SEPARATOR}${tahunInvoice}"
        }
    }
}