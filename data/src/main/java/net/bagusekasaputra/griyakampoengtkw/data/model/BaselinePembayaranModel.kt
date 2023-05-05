package net.bagusekasaputra.griyakampoengtkw.data.model

data class BaselinePembayaranModel(
    val kavling: String = "",
    val jumlahUang: Long = 0L,
    var timeMillis: Long = System.currentTimeMillis()
)
