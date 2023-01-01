package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getCustomRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getMonthlyRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getTahunSekarang
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getWeeklyRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.isWithinRange
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import java.util.*

data class BiayaMarketing(
    var id: Long? = 0L, // local database identifier
    val kavlingKode: String,
    val tanggal: String,
    val jenisBiaya: String,
    val harga: String,
    var totalBiaya: String = "",
) {
    companion object {

        fun hitungTotalBiayaMarketing(listBiayaMarketing: List<BiayaMarketing>): Long {
            var totalBiayaMarketing = 0L

            listBiayaMarketing.forEach {
                totalBiayaMarketing += NumberUtil.formatStringToLong(it.harga)
            }

            return totalBiayaMarketing
        }

        fun List<BiayaMarketing>?.filterPeriode(
            periode: PeriodeRekap,
            start: Date?,
            end: Date?,
        ): List<BiayaMarketing>? {
            return when (periode) {
                PeriodeRekap.SEMUA -> this
                PeriodeRekap.TAHUN_INI -> this?.filter {
                    val tahunBiayaMarketing = Calendar.getInstance().run {
                        time = it.tanggal.toDate()

                        get(Calendar.YEAR)
                    }

                    tahunBiayaMarketing == getTahunSekarang()
                }
                PeriodeRekap.BULAN_INI -> this?.filter {
                    val rangeBulan = getMonthlyRangeDate()
                    val startDate = rangeBulan[0]
                    val endDate = rangeBulan[1]

                    it.tanggal.toDate().isWithinRange(startDate, endDate)
                }
                PeriodeRekap.MINGGU_INI -> this?.filter {
                    val rangeMinggu = getWeeklyRangeDate()
                    val startDate = rangeMinggu[0]
                    val endDate = rangeMinggu[1]

                    it.tanggal.toDate().isWithinRange(startDate, endDate)
                }
                PeriodeRekap.CUSTOM -> this?.filter {
                    val rangeTanggal = getCustomRangeDate(start!!, end!!)
                    val startDate = rangeTanggal[0]
                    val endDate = rangeTanggal[1]

                    it.tanggal.toDate().isWithinRange(startDate, endDate)
                }
            }
        }
    }
}