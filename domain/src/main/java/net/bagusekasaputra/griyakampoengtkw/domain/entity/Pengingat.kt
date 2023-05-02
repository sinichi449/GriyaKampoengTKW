package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class Pengingat(
    var id: Long? = null,
    val title: String,
    val content: String,
    val date: String,
    val time: String,
    var isActive: Boolean,
)