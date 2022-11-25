package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class BiayaMarketing(
    var id: Long? = 0L, // local database identifier
    val kavlingKode: String,
    val tanggal: String,
    val jenisBiaya: String,
    val harga: String,
    var totalBiaya: String = "",
) {
}