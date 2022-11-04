package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class Pembayaran(
    val termin: String,
    val tanggal: String,
    val jumlahUangDibayar: String = "",
    var totalUangMasuk: String = "",
    var presentase: Double = 0.0,
    val keterangan: String
) {
}