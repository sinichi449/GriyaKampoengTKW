package net.bagusekasaputra.griyakampoengtkw.domain.entity

import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class ProgressKavlingTest {

    @Test
    fun whenUangMasukBulanIniIsLessThanZero_shouldNotShowIconSudahBayar_adaPembayaranIsFalse() {
        val progressKavling = ProgressKavling(
            kavling = "",
            angsuranBulanan = 0,
            uangMasukBulanIni = Random.nextLong(from = -128, until = 0)
        )

        Assert.assertEquals(false, progressKavling.adaPembayaran)
    }

    @Test
    fun whenAngsuranBulananAndUangMasukBulanIniIsZero_shouldReturnZeroPersentase() {
        val progressKavling = ProgressKavling(
            kavling = "",
            angsuranBulanan = 0,
            uangMasukBulanIni = 0,
        )

        Assert.assertEquals(0, progressKavling.persentaseBulanIni())
    }

}