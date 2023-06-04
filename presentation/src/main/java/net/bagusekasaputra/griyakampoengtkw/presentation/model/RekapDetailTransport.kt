package net.bagusekasaputra.griyakampoengtkw.presentation.model

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * For transporting Rekap Detail business... Idk..
 */
data class RekapDetailTransport(
    val rekapType: RekapType?,
    val periodeRekap: PeriodeRekap?,
    val startDate: Date?,
    val endDate: Date?,
    val includeDataLama: Boolean,
): java.io.Serializable {
    fun getRangeTanggal(): String {
        return when (periodeRekap) {
            PeriodeRekap.SEMUA -> "Semua"
            PeriodeRekap.MINGGU_INI -> {
                val calendar = Calendar.getInstance()
                val rangeTanggal = DateUtil.getWeeklyRangeDate()

                calendar.time = rangeTanggal[0]
                val tahun = calendar.get(Calendar.YEAR)

                val bulanAwal = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
                val tanggalAwal = calendar.get(Calendar.DAY_OF_MONTH)

                calendar.time = rangeTanggal[1]
                val bulanAkhir = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
                val tanggalAkhir = calendar.get(Calendar.DAY_OF_MONTH)

                "$tanggalAwal $bulanAwal - $tanggalAkhir $bulanAkhir $tahun"
            }
            PeriodeRekap.BULAN_INI -> {
                val calendar = Calendar.getInstance()
                val bulan = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)
                val tahun = calendar.get(Calendar.YEAR)

                "$bulan $tahun"
            }
            PeriodeRekap.TAHUN_INI -> {
                val calendar = Calendar.getInstance()
                val tahun = calendar.get(Calendar.YEAR)

                "$tahun"
            }
            PeriodeRekap.CUSTOM -> {
                val calendar = Calendar.getInstance()

                calendar.time = startDate!!
                val tahunStart = calendar.get(Calendar.YEAR)
                val bulanStart = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
                val tanggalStart = calendar.get(Calendar.DAY_OF_MONTH)

                calendar.time = endDate!!
                val tahunEnd = calendar.get(Calendar.YEAR)
                val bulanEnd = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
                val tanggalEnd = calendar.get(Calendar.DAY_OF_MONTH)

                if (tahunStart == tahunEnd) {
                    if (bulanStart == bulanEnd) {
                        "$tanggalStart - $tanggalEnd $bulanStart $tahunStart"
                    } else {
                        "$tanggalStart $bulanStart - $tanggalEnd $bulanEnd $tahunStart"
                    }
                } else {
                    "$tanggalStart $bulanStart $tahunStart - $tanggalEnd $bulanEnd $tahunEnd"
                }
            }
            null -> "NULL"
        }
    }
}