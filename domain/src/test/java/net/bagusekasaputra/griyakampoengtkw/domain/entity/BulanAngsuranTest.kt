package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class BulanAngsuranTest {

    @Test
    fun parseBulanAngsuran_fromString() {
        val separator1 = "-"
        val invoiceUntuk1 = "06${separator1}2023"
        val bulanAngsuran1 = BulanAngsuran.fromString(invoiceUntuk1, separator1)

        val result1 = BulanAngsuran(6, 2023)
        Assert.assertEquals(result1, bulanAngsuran1)

        val separator2 = "/"
        val randomBulan = Random.nextInt(from = 1, until = 12)
        val randomTahun = Random.nextInt(from = 1900, until = 2023)
        val invoiceUntuk2 = "${randomBulan}${separator2}${randomTahun}"
        val bulanAngsuran2 = BulanAngsuran.fromString(invoiceUntuk2, separator2)

        val result2 = BulanAngsuran(randomBulan, randomTahun)
        Assert.assertEquals(result2, bulanAngsuran2)
    }

}