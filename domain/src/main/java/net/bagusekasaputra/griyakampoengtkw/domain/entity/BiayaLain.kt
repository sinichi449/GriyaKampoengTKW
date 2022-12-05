package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil

data class BiayaLain(
    val id: Long? = null,
    val jenisBiaya: String,
    val harga: Long,
    val tanggal: String,
) {

    val parsedHarga = NumberUtil.formatLongToString(harga)

    companion object {
        fun hitungTotalBiayaLain(listBiayaLain: List<BiayaLain>?): Long {
            var totalBiayaLain = 0L

            listBiayaLain?.forEach {
                totalBiayaLain += it.harga
            }

            return totalBiayaLain
        }
    }
}