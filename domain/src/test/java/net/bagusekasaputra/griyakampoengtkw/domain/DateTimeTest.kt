package net.bagusekasaputra.griyakampoengtkw.domain

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import org.junit.Test

class DateTimeTest {

    @Test
    fun test_blabalbal() {
        val tanggalBeli = "10/01/2023".toDate()
        val baselinePembayaran = BaselinePembayaran("", 48, 0L, 0)

        println(baselinePembayaran.hitungSisaBulanAngsuran(tanggalBeli))
    }

    private fun hitungSisaBulanAngsuran(bulanBeli: Int, tahunBeli: Int) {
//        val tanggalBeli = Calendar.getInstance().apply {
//
//        }
//        val tanggalBerakhir =
    }
}