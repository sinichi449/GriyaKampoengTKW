package net.bagusekasaputra.griyakampoengtkw.domain

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.isWithinRange
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
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
}