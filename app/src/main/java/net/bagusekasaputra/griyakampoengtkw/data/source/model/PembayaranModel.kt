package net.bagusekasaputra.griyakampoengtkw.data.source.model

data class PembayaranModel(
    val termin: String = "",
    val tanggal: String = "",
    val jumlahUangDibayar: Long = 0L,
    var totalUangMasuk: Long = 0L,
    var presentase: Double = 0.0,
    val keterangan: String = "",
)