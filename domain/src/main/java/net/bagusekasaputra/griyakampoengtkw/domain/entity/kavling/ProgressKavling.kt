package net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling

import java.math.BigDecimal
import java.math.RoundingMode

data class ProgressKavling(
    val kavling: String,
    val angsuranBulanan: Long,
    val uangMasukBulanIni: Long,
) {
    val adaPembayaran = uangMasukBulanIni > 0L

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

    companion object {
        fun EMPTY(kavling: String): ProgressKavling {
            return ProgressKavling(
                kavling = kavling,
                angsuranBulanan = 0L,
                uangMasukBulanIni = 0L,
            )
        }
    }

}