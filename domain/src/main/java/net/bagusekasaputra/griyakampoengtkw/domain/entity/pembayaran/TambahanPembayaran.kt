package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran

import java.util.Date
import java.util.UUID

data class TambahanPembayaran(
    val id: String = UUID.randomUUID().toString(),
    val kavling: String,
    val kategori: Kategori,
    val tanggal: Date,
    val jumlahUang: Long,
    val sudahIsiFoto: Boolean,
    val keterangan: String,
    val timeMillis: Long = System.currentTimeMillis(),
) {

    enum class Kategori(val kode: String) {
        LUASAN("L"),
        PEMBANGUNAN("P"),
    }
}