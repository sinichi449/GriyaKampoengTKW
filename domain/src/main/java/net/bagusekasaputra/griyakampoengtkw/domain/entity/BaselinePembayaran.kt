package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import java.math.BigDecimal
import java.math.RoundingMode

data class BaselinePembayaran(
    val id: Long = 0L,
    val kavling: String,
    val jumlahUang: Long,
    var timeMillis: Long = System.currentTimeMillis()
) {
    val parsedJumlahUang = NumberUtil.formatLongToString(jumlahUang)

    companion object {
        fun hitungAngsuranPerBulan(hargaKavling: HargaKavling, opsiTahun: Int): Double {
            // Tambah Luasan doesn't included
            val mHargaKavling = BigDecimal(hargaKavling.hargaLong)
            val mOpsiTahun = BigDecimal(opsiTahun * 12) // Multiplied by 12 to convert to Bulan

            val mAngsuranPerBulan = mHargaKavling.divide(mOpsiTahun, 2, RoundingMode.HALF_UP)

            return mAngsuranPerBulan.toDouble()
        }
    }
}