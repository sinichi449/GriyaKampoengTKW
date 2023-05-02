package net.bagusekasaputra.griyakampoengtkw.data.model

data class RekapUangMasukModel(
    val noKavling: String = "",
    val namaCostumer: String = "",
    val tanggal: String = "",
    val jenisPembayaran: String = "",
    val jumlahPembayaran: Long = 0L,
)