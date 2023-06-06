package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil

data class HargaKavling(
    val kavlingKode: String,
    val harga: String,
    val tambahanLuas: String,
) {
    val hargaLong = NumberUtil.formatStringToLong(this.harga)
    val tambahLuasanLong = NumberUtil.formatStringToLong(this.tambahanLuas)
    val hargaDanTambahLuasan = hargaLong + tambahLuasanLong

    operator fun minus(other: Long): Long {
        return hargaDanTambahLuasan - other
    }

    fun toFloat(): Float {
        return hargaDanTambahLuasan.toFloat()
    }

    companion object {
        fun EMPTY(kavling: String): HargaKavling {
            return HargaKavling(
                kavlingKode = kavling,
                harga = "0",
                tambahanLuas = "0",
            )
        }
    }
}