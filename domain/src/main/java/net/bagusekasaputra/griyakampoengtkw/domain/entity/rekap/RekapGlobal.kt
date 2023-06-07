package net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date


data class RekapGlobal(
    val namaCostumer: String,
    val noKavling: String,
    val tanggalPembelian: Date? = null,
    val harga: Long,
    val jumlahUangMasuk: Long,
) {
    val sisaPembayaran = harga - jumlahUangMasuk
    val persentase = hitungPersentase()

    private fun hitungPersentase(): Double {
        // Error division by zero
        return if (harga > 0) {
            val mJumlahUangMasuk = BigDecimal(jumlahUangMasuk)
            val mHarga = BigDecimal(harga)

            val mPersentase = mJumlahUangMasuk.divide(mHarga, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))

            mPersentase.toDouble()
        } else {
            0.0
        }
    }

    val parsedHarga = NumberUtil.formatLongToString(harga)
    val parsedJumlahUangMasuk = NumberUtil.formatLongToString(jumlahUangMasuk)
    val parsedSisaPembayaran = NumberUtil.formatLongToString(sisaPembayaran)
    val parsedPersentase = "$persentase%"
}
