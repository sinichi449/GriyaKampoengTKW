package net.bagusekasaputra.griyakampoengtkw.domain.entity

import java.util.UUID

data class PembayaranTambahLuasan(
    val id: String = UUID.randomUUID().toString(),
    val kavling: String,
    val tanggal: String,
    val jumlahUang: Long,
    val timeMillis: Long = System.currentTimeMillis()
)