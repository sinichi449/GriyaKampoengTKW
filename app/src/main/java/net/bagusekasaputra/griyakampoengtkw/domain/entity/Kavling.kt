package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class Kavling(
    val kode: String,
    val isActive: Boolean = true,
    val warna: String,
    val ukuran: String,
    val type: String,
)

