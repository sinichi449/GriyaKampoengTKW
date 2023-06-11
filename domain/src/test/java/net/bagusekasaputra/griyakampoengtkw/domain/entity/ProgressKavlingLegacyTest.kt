package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.ProgressKavlingLegacy
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class ProgressKavlingLegacyTest {

    @Test
    fun whenUangMasukBulanIniIsLessThanZero_shouldNotShowIconSudahBayar_adaPembayaranIsFalse() {
        val progressKavlingLegacy = ProgressKavlingLegacy(
            kavling = "",
            angsuranBulanan = 0,
            uangMasukBulanIni = Random.nextLong(from = -128, until = 0)
        )

        Assert.assertEquals(false, progressKavlingLegacy.adaPembayaran)
    }

    @Test
    fun whenAngsuranBulananAndUangMasukBulanIniIsZero_shouldReturnZeroPersentase() {
        val progressKavlingLegacy = ProgressKavlingLegacy(
            kavling = "",
            angsuranBulanan = 0,
            uangMasukBulanIni = 0,
        )

        Assert.assertEquals(0, progressKavlingLegacy.persentaseBulanIni())
    }

}