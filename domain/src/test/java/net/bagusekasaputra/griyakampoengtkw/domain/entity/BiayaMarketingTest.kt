package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.normalize
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import java.util.Calendar

class BiayaMarketingTest {

    @Test
    fun filterPeriodeBulanIni_shouldCorrect() {
        val calendar = Calendar.getInstance().normalize()
        val totalPembayaran: Int
        val startDate = calendar.run {
            set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))

            time
        }
        val endDate = calendar.run {
            totalPembayaran = getActualMaximum(Calendar.DAY_OF_MONTH)
            set(Calendar.DAY_OF_MONTH, totalPembayaran)

            time
        }

        val generatedDateList = DateUtil.getListDate(startDate, endDate)
        Assert.assertEquals(totalPembayaran, generatedDateList.size)

        val biayaMarketingList = mutableListOf<BiayaMarketing>().apply {
            generatedDateList.forEach { tanggal ->
                add(BiayaMarketing(
                    kavlingKode = anyString(),
                    tanggal = tanggal.toSlashedString(),
                    jenisBiaya = anyString(),
                    harga = NumberUtil.formatLongToString(anyLong()),
                ))
            }
        }
        val bulanIniBiayaMarketingList = biayaMarketingList.filterPeriode(PeriodeRekap.BULAN_INI, null, null)!!

        Assert.assertEquals(totalPembayaran, bulanIniBiayaMarketingList.size)
    }

    @Test
    fun filterPeriodeMingguIni_shouldCorrect() {
        val calendar = Calendar.getInstance()
        val startDate = calendar.run {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

            time
        }
        val endDate = calendar.run {
            add(Calendar.DAY_OF_WEEK, 7)

            time
        }
        val generatedDateList = DateUtil.getListDate(startDate, endDate)
        Assert.assertEquals(8, generatedDateList.size)

        val biayaMarketingList = mutableListOf<BiayaMarketing>().apply {
            generatedDateList.forEach { tanggal ->
                add(BiayaMarketing(
                    kavlingKode = anyString(),
                    tanggal = tanggal.toSlashedString(),
                    jenisBiaya = anyString(),
                    harga = NumberUtil.formatLongToString(anyLong()),
                ))
            }
        }
        val mingguIniBiayaMarketing = biayaMarketingList.filterPeriode(PeriodeRekap.MINGGU_INI, null, null)

        Assert.assertEquals(generatedDateList.size, mingguIniBiayaMarketing?.size)
    }
}