package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil

data class HargaKavling(
    val kavlingKode: String,
    val harga: String,
    val tambahanLuas: String,
) {
    private val hargaLong = NumberUtil.formatStringToLong(this.harga)
    private val tambahLuasanLong = NumberUtil.formatStringToLong(this.tambahanLuas)
    private val total = hargaLong + tambahLuasanLong

    operator fun minus(other: Long): Long {
        return total - other
    }

    fun toFloat(): Float {
        return total.toFloat()
    }
}