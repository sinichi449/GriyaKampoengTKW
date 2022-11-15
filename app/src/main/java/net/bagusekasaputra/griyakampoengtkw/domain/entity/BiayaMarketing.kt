package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class BiayaMarketing(
    var nomor: Int? = null,
    val kavlingKode: String,
    val jenisBiaya: String,
    val harga: String,
    var totalBiaya: String = "",
) {
}