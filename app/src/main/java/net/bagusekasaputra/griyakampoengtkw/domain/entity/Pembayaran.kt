package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class Pembayaran(
    val termin: String,
    val tanggal: String,
    val jumlahUangDibayar: String = "",
    var totalUangMasuk: String = "",
    var presentase: Double = 0.0,
    var sisaBelumTerbayar: String = "",
    val keterangan: String,
    val timeMillis: Long,
    var sudahIsiFotoPembayaran: Boolean = false,
) {
}