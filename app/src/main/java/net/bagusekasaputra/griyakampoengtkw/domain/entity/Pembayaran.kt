package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class Pembayaran(
    val termin: String,
    val tanggal: String,
    val jumlahUang: Int,
    val totalUangMasuk: Int,
    val presentase: Double,
    val keterangan: String
)