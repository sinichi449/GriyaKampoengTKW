package net.bagusekasaputra.griyakampoengtkw.presentation.util

import net.bagusekasaputra.griyakampoengtkw.presentation.resetHours
import java.util.*

object DateUtil {

    // List Periode
    const val MINGGU_INI = 0
    const val BULAN_INI = 1
    const val TAHUN_INI = 2

    fun filterPeriode(periode: Int, listDate: List<Date>): List<Date> {
        val listRangePeriode = generateRangeDate(periode)

        return listDate
            .map {
                // Set the hours to 00:00:00
                Calendar.getInstance()
                    .apply { time = it }
                    .resetHours()
                    .time
            }
            .filter {
                listRangePeriode.contains(it)
            }
    }

    private fun generateRangeDate(periode: Int): List<Date> {
        val hariIni = Calendar.getInstance().resetHours()

        val specifiedPeriode = Calendar.getInstance().apply {
            when (periode) {
                MINGGU_INI -> add(Calendar.DAY_OF_MONTH, -7)
                BULAN_INI -> set(Calendar.DAY_OF_MONTH, 1) // set to first date of specific month
                TAHUN_INI -> {
                    // set to January 1st
                    set(Calendar.MONTH, Calendar.JANUARY)
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                else -> throw Exception("Gagal mengeksekusi DateUtil.generateRangeDate() -> Periode kode $periode tak diketahui.")
            }
        }
            .resetHours()

        val listTanggal = mutableListOf<Date>()
        // initial
        listTanggal.add(specifiedPeriode.time)
        while (specifiedPeriode < hariIni) {
            val temp = Calendar.getInstance().apply {
                specifiedPeriode.add(Calendar.DAY_OF_MONTH, 1)

                time = specifiedPeriode.time
            }
            listTanggal.add(temp.time)
        }

        return listTanggal
    }
}