package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getCustomRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getMonthlyRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getTahunSekarang
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getWeeklyRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.isWithinRange
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import java.util.Calendar
import java.util.Date

data class BiayaLain(
    val id: Long? = null,
    val jenisBiaya: String,
    val harga: Long,
    val tanggal: String,
) {
    val parsedHarga = NumberUtil.formatLongToString(harga)

    enum class SortMethod {
        JENIS_BIAYA,
        HARGA,
        TANGGAL
    }

    companion object {
        fun hitungTotalBiayaLain(listBiayaLain: List<BiayaLain>?): Long {
            var totalBiayaLain = 0L

            listBiayaLain?.forEach {
                totalBiayaLain += it.harga
            }

            return totalBiayaLain
        }

        fun List<BiayaLain>?.filterPeriode(
            periode: PeriodeRekap,
            start: Date?,
            end: Date?,
        ): List<BiayaLain>? {
            return when (periode) {
                PeriodeRekap.SEMUA -> this
                PeriodeRekap.TAHUN_INI -> this?.filter {
                    val tahunBiayaLain = Calendar.getInstance().run {
                        time = it.tanggal.toDate()

                        get(Calendar.YEAR)
                    }

                    getTahunSekarang() == tahunBiayaLain
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

        fun List<BiayaLain>?.sort(sortMethod: SortMethod): List<BiayaLain>? {
            return when (sortMethod) {
                SortMethod.TANGGAL -> {
                    this?.sortedBy {
                        Calendar.getInstance().apply {
                            time = it.tanggal.toDate()
                        }.timeInMillis
                    }
                }
                SortMethod.HARGA -> {
                    this?.sortedBy {
                        it.harga
                    }
                }
                SortMethod.JENIS_BIAYA -> {
                    this?.sortedBy {
                        it.jenisBiaya
                    }
                }
            }
        }
    }
}