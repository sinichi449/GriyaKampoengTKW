package net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap

enum class PeriodeRekap {
    SEMUA,
    TAHUN_INI,
    BULAN_INI,
    MINGGU_INI,
    CUSTOM,
}

fun getPeriodeRekap(periode: String): PeriodeRekap? {
    return when(periode) {
        "SEMUA" -> PeriodeRekap.SEMUA
        "TAHUN_INI" -> PeriodeRekap.TAHUN_INI
        "BULAN_INI" -> PeriodeRekap.BULAN_INI
        "MINGGU_INI" -> PeriodeRekap.MINGGU_INI
        "CUSTOM" -> PeriodeRekap.CUSTOM
        else -> null
    }
}