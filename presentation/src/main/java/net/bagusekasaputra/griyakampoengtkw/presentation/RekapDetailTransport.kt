package net.bagusekasaputra.griyakampoengtkw.presentation

import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap.RekapType
import java.util.Date


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
        return if (periodeRekap == PeriodeRekap.SEMUA) {
            "Semua"
        } else {
            "${startDate?.toSlashedDate()} - ${endDate?.toSlashedDate()}"
        }
    }
}