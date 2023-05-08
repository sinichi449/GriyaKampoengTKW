package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import java.math.BigDecimal
import java.math.RoundingMode

data class BaselinePembayaran(
    val kavling: String,
    val opsiBulan: Int,
    val jumlahUang: Long,
    val tanggalPembayaranMaks: Int,
    var timeMillis: Long = System.currentTimeMillis()
) {
    val parsedJumlahUang = NumberUtil.formatLongToString(jumlahUang)

    companion object {
        fun hitungAngsuranPerBulan(hargaKavling: HargaKavling, timeframeBulan: Int): Double {
            // Tambah Luasan doesn't included
            val mHargaKavling = BigDecimal(hargaKavling.hargaLong)
            val mTimeFrameBulan = BigDecimal(timeframeBulan)

            val mAngsuranPerBulan = mHargaKavling.divide(mTimeFrameBulan, 2, RoundingMode.HALF_UP)

            return mAngsuranPerBulan.toDouble()
        }
    }
}