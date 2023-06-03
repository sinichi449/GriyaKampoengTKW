package net.bagusekasaputra.griyakampoengtkw.domain

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.isWithinRange
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toStringAndLongBulan
import org.junit.Assert
import org.junit.Test
import java.util.Calendar

class DateTest {

    @Test
    fun isWithinRange_shouldCorrect() {
        val tanggalSatuJuni = "1/6/2023".toDate()
        val rangeTanggalJuni = DateUtil.getMonthlyRangeDate(Calendar.JUNE, 2023)

        Assert.assertEquals("1/6/2023", rangeTanggalJuni[0].toSlashedString())
        Assert.assertEquals(true, tanggalSatuJuni.isWithinRange(rangeTanggalJuni[0], rangeTanggalJuni[1]))
    }

    @Test
    fun listDateGivenStartAndEnd_shouldCorrect() {
        val startDate = "1/1/2020".toDate()
        val endDate = "3/6/2023".toDate()

        val dateList = DateUtil.getListDate(startDate, endDate)

        Assert.assertEquals(dateList.first(), startDate)
        Assert.assertEquals(dateList.last(), endDate)
        Assert.assertEquals(1250, dateList.size)
    }

    @Test
    fun currentMonthlyRange_shouldCorrect() {
        val calendar = Calendar.getInstance()
        val awalTanggalBulanSekarang = calendar.run {
            set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))

            time
        }
        val akhirTanggalBulanSekarang = calendar.run {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))

            time
        }

        val monthlyRange = DateUtil.getMonthlyRangeDate()

        val awalResultRange = monthlyRange.first().toSlashedString()
        val akhirResultRange = monthlyRange.last().toSlashedString()

        Assert.assertEquals(awalTanggalBulanSekarang.toSlashedString(), awalResultRange)
        Assert.assertEquals(akhirTanggalBulanSekarang.toSlashedString(), akhirResultRange)
    }

    @Test
    fun currentWeeklyRange_shouldCorrect() {
        val calendar = Calendar.getInstance()
        val awalTanggalMingguIni = calendar.run {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

            time
        }
        val akhirTanggalMingguIni = calendar.run {
            add(Calendar.DAY_OF_WEEK, 7)

            time
        }

        val weeklyRange = DateUtil.getWeeklyRangeDate()

        val awalResultRange = weeklyRange.first().toSlashedString()
        val akhirResultRange = weeklyRange.last().toSlashedString()

        Assert.assertEquals(awalTanggalMingguIni.toSlashedString(), awalResultRange)
        Assert.assertEquals(akhirTanggalMingguIni.toSlashedString(), akhirResultRange)
    }

    @Test
    fun convertDateToStringLongBulan_shouldCorrect() {
        val startDate = "1/1/2020".toDate()
        val endDate = "3/6/2023".toDate()
        val generatedTanggalList = DateUtil.getListDate(startDate, endDate)

        val calendar = Calendar.getInstance()
        generatedTanggalList.forEach {
            calendar.time = it

            val tanggal = calendar.get(Calendar.DAY_OF_MONTH)
            val bulan = DateUtil.namaBulanLong(calendar.get(Calendar.MONTH) + 1)
            val tahun = calendar.get(Calendar.YEAR)
            val correctTanggal = "$tanggal $bulan $tahun"

            val result = it.toStringAndLongBulan()

            Assert.assertEquals(correctTanggal, result)
        }
    }

    @Test
    fun isWithinRange_edgeTest_shouldCorrect() {
        val tanggal = "1/6/2023".toDate()
        val startTanggal = "1/6/2023".toDate()
        val endTanggal = "3/6/2023".toDate()

        Assert.assertEquals(true, tanggal.isWithinRange(startTanggal, endTanggal))
    }
}