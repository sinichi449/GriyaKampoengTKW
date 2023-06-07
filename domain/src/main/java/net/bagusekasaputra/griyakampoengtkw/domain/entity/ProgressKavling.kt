package net.bagusekasaputra.griyakampoengtkw.domain.entity

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Progress kavling is based on Pembayaran.BulanAngsuran as opposed to Pembayaran.tanggal.
 */
data class ProgressKavling(
    val kavling: String,
    val angsuranBulanan: Long,
    val uangMasukBulanIni: Long,
) {

    fun persentaseBulanIni(): Int {
        return if (angsuranBulanan > 0L && uangMasukBulanIni > 0L) {
            val mAngsuran = BigDecimal(angsuranBulanan)
            val mUangMasuk = BigDecimal(uangMasukBulanIni)

            val persentase = mUangMasuk
                .divide(mAngsuran, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
                .toInt()

            if (persentase > 100) {
                return 100
            }
            return persentase
        } else {
            0
        }
    }

}