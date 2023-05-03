package net.bagusekasaputra.griyakampoengtkw.domain.entity

import java.util.Date

data class BiayaPribadi(
    val id: Long = 0L,
    val jenisBiaya: String,
    val tanggal: Date,
    val harga: Long,
    var lastModified: Long = System.currentTimeMillis()
) {

    companion object {
        fun sortByTanggal(listBiayaPribadi: List<BiayaPribadi>): List<BiayaPribadi> {
            return listBiayaPribadi.sortedBy {
                it.tanggal.time
            }
        }
    }
}