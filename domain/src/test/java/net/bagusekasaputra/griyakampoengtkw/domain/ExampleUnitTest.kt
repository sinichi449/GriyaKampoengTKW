package net.bagusekasaputra.griyakampoengtkw.domain

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
}