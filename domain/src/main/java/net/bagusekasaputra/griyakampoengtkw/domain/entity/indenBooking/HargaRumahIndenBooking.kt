package net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling

/**
 * Inden-Booking only
 */
data class HargaRumahIndenBooking(
    val harga: Long,
    val tambahLuasan: Long,
    var keyId: String = "",
) {

    fun toHargaKavling(kavling: String): HargaKavling {
        return HargaKavling(
            kavlingKode = kavling,
            harga = NumberUtil.formatLongToString(harga),
            tambahanLuas = NumberUtil.formatLongToString(tambahLuasan),
        )
    }
}
