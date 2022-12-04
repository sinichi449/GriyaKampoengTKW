package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil

data class BiayaMarketing(
    var id: Long? = 0L, // local database identifier
    val kavlingKode: String,
    val tanggal: String,
    val jenisBiaya: String,
    val harga: String,
    var totalBiaya: String = "",
) {
    companion object {

        fun hitungTotalBiayaMarketing(listBiayaMarketing: List<BiayaMarketing>): Long {
            var totalBiayaMarketing = 0L

            listBiayaMarketing.forEach {
                totalBiayaMarketing += NumberUtil.formatStringToLong(it.harga)
            }

            return totalBiayaMarketing
        }

    }
}