package net.bagusekasaputra.griyakampoengtkw.data.model

data class TambahanPembayaranModel(
    val id: String = "",
    val kavling: String = "",
    val kategori: String = "",
    val tanggal: String = "",
    val jumlahUang: Long = 0L,
    val keterangan: String = "",
    val timeMillis: Long = 0L,
)