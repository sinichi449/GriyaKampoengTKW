package net.bagusekasaputra.griyakampoengtkw.data.source.model

data class PembayaranModel(
    val termin: String = "",
    val tanggal: String = "",
    val jumlahUang: Long = 0L,
    val totalUangMasuk: Long = 0L,
    val presentase: Double = 0.0,
    val keterangan: String = "",
)