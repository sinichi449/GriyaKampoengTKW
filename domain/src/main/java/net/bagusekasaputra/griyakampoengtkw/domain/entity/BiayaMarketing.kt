package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class BiayaMarketing(
    var id: Long? = 0L,
    var timeMillis: Long? = null,
    val kavlingKode: String,
    val jenisBiaya: String,
    val harga: String,
    var totalBiaya: String = "",
) {
}