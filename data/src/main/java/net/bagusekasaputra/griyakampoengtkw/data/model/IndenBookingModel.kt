package net.bagusekasaputra.griyakampoengtkw.data.model

data class IndenBookingModel(
    val timeMillis: Long = 0L,
    val namaCostumer: String = "",
    val tanggalDibayar: String = "",
    val jumlahUang: Long = 0L,
    val noHp: String = "",
    val keterangan: String = "",
)
