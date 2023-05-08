package net.bagusekasaputra.griyakampoengtkw.data.model

data class BaselinePembayaranModel(
    val kavling: String = "",
    val opsiBulan: Int = 0,
    val jumlahUang: Long = 0L,
    val tanggalPembayaranMaks: Int = 0,
    var timeMillis: Long = 0L,
)
