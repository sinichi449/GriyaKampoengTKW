package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import org.junit.Assert
import org.junit.Test

class BaselinePembayaranTest {

    @Test
    fun tanggalSelesaiAngsuran_timeFrameTahun_shouldCorrect() {
        val tanggalPembelian = "03/06/2023".toDate()
        val timeFrameTahun = 4
        val tanggalSelesai = BaselinePembayaran.hitungTanggalAngsuranSelesai(
            tanggalPembelian, BaselinePembayaran.OPSI_TIMEFRAME_TAHUN, timeFrameTahun
        )

        Assert.assertEquals("03/06/2027", tanggalSelesai.toSlashedString())
    }

    @Test
    fun tanggalSelesaiAngsuran_timeFrameBulan_shouldCorrect() {
        val tanggalPembelian = "03/06/2023".toDate()
        val timeFrameBulan = 15
        val tanggalSelesai = BaselinePembayaran.hitungTanggalAngsuranSelesai(
            tanggalPembelian, BaselinePembayaran.OPSI_TIMEFRAME_BULAN, timeFrameBulan
        )

        Assert.assertEquals("03/09/2024", tanggalSelesai.toSlashedString())
    }
}