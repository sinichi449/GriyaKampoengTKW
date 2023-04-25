package net.bagusekasaputra.griyakampoengtkw.domain

import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapGlobal
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_tanggal() {
        val tanggalStr = "25/11/2022"

        val formatToCalendar = tanggalStr.split("/").let {
            val tanggal = it[0].toInt()
            val bulan = it[1].toInt() - 1 // the index of calendar, I assume, is starting from 0 for January
            val tahun = it[2].toInt()

            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, tanggal)
                set(Calendar.MONTH, bulan)
                set(Calendar.YEAR, tahun)
            }
        }

        assertEquals(25, formatToCalendar.get(Calendar.DAY_OF_MONTH))

    }

    @Test
    fun test_rekap_global_entity_sudah_presisi() {
        val listRekapGlobal = listOf(
            RekapGlobal("Andi Setya Budi", "2", "06/05/2021", 250000000L, 130000000L),
            RekapGlobal("Iwan Ferdiyanto", "3", "01/05/2021", 210000000L, 30000000L),
            RekapGlobal("Norma Fiki Sugiarto", "4", "13/07/2022", 230000000L, 102087227L),
        )

        listRekapGlobal.forEach {
            println("--------------------------------------------------------------------------------------")
            println("Sisa Pembayaran -> ${it.parsedSisaPembayaran}")
            println("Persentase -> ${it.parsedPersentase}")
            println("--------------------------------------------------------------------------------------")
        }

        assertEquals("120,000,000", listRekapGlobal[0].parsedSisaPembayaran)
        assertEquals("180,000,000", listRekapGlobal[1].parsedSisaPembayaran)
        assertEquals("127,912,773", listRekapGlobal[2].parsedSisaPembayaran)
    }
}