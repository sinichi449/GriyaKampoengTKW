package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class Pembayaran(
    val termin: String,
    val tanggal: String,
    val jumlahUang: String,
    val totalUangMasuk: String,
    val presentase: Double,
    val keterangan: String
) {
}