package net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import java.math.BigDecimal
import java.math.RoundingMode

data class SisaPembayaran(
    val kavling: String,
    val namaCostumer: String,
    val hargaKavling: HargaKavling,
    val totalUangMasuk: Long,
) {
    val sisaBelumDibayar = hargaKavling.hargaDanTambahLuasan - totalUangMasuk

    fun getPresentase(): Double {
        val mTotalUangMasuk = BigDecimal(totalUangMasuk)
        val mHargaDanTambahLuasan = BigDecimal(hargaKavling.hargaDanTambahLuasan)

        val persentase = mTotalUangMasuk
            .divide(mHargaDanTambahLuasan, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))

        return persentase.toDouble()
    }

    fun getParsedTotalUangMasuk(): String {
        return NumberUtil.formatLongToString(totalUangMasuk)
    }

    fun getParsedSisaBelumDibayar(): String {
        return NumberUtil.formatLongToString(sisaBelumDibayar)
    }
}
