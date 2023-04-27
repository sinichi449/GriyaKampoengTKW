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

data class FeeMarketing(
    val kavlingKode: String,
    val namaMarketer: String,
    var biayaMarketer: String,
    var tanggalPenerimaan: String,
) {
    val parsedBiayaMarketer = NumberUtil.formatStringToLong(biayaMarketer)

    fun getTimemillisTanggalPenerimaan(): Long {
        val formatToCalendar = tanggalPenerimaan.split("/").let {
            val tanggal = it[0].toInt()
            val bulan = it[1].toInt() - 1 // the index of calendar, I assume, is starting from 0 for January
            val tahun = it[2].toInt()

            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, tanggal)
                set(Calendar.MONTH, bulan)
                set(Calendar.YEAR, tahun)
            }
        }

        return formatToCalendar.timeInMillis
    }

    companion object {
        fun FeeMarketing?.filterPeriode(
            periode: PeriodeRekap,
            start: Date?,
            end: Date?,
        ): FeeMarketing? {
            return this?.let {
                val tanggalPenerimaan = it.tanggalPenerimaan.toDate()

                when(periode) {
                    PeriodeRekap.SEMUA -> it
                    PeriodeRekap.TAHUN_INI -> {
                        val tahunPenerimaan = Calendar.getInstance().run {
                            time = tanggalPenerimaan

                            get(Calendar.YEAR)
                        }

                        if (getTahunSekarang() == tahunPenerimaan) it else null
                    }
                    PeriodeRekap.BULAN_INI -> {
                        val rangeTanggal = getMonthlyRangeDate()
                        val startDate = rangeTanggal[0]
                        val endDate = rangeTanggal[1]

                        if (tanggalPenerimaan.isWithinRange(startDate, endDate)) it else null
                    }
                    PeriodeRekap.MINGGU_INI -> {
                        val rangeTanggal = getWeeklyRangeDate()
                        val startDate = rangeTanggal[0]
                        val endDate = rangeTanggal[1]

                        if (tanggalPenerimaan.isWithinRange(startDate, endDate)) it else null
                    }
                    PeriodeRekap.CUSTOM -> {
                        val rangeTanggal = getCustomRangeDate(start!!, end!!)
                        val startDate = rangeTanggal[0]
                        val endDate = rangeTanggal[1]

                        if (tanggalPenerimaan.isWithinRange(startDate, endDate)) it else null
                    }
                }
            }
        }

        fun hitungTotalAllKavling(mapFeeMarketing: Map<String, FeeMarketing?>): Long {
            var mTotal = 0L

            mapFeeMarketing.keys.forEach { kavling ->
                val feeMarketing = mapFeeMarketing[kavling]

                mTotal += feeMarketing?.parsedBiayaMarketer ?: 0L
            }

            return mTotal
        }
    }
}