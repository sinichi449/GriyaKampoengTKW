package net.bagusekasaputra.griyakampoengtkw.domain.entity

import java.util.Date

data class IndenBooking(
    val id: Long = 0L,
    val namaCostumer: String,
    val tanggalDibayar: Date,
    val jumlahUang: Long,
    val noHp: String,
    val keterangan: String = "-",
    var timeMillis: Long = System.currentTimeMillis(),
) {

    companion object {
        fun sortByTanggalDibayar(listIndenBooking: List<IndenBooking>?): List<IndenBooking> {
            return listIndenBooking?.sortedBy {
                it.tanggalDibayar
            } ?: emptyList()
        }
    }
}