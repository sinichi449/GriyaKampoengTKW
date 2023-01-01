package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap

enum class RekapType {
    Besar,
    Global,
    UangMasuk,
    FeeMarketing,
    BiayaMarketing,
}

fun getRekapType(str: String): RekapType? {
    return when(str) {
        "Besar" -> RekapType.Besar
        "Global" -> RekapType.Global
        "UangMasuk" -> RekapType.UangMasuk
        "FeeMarketing" -> RekapType.FeeMarketing
        "BiayaMarketing" -> RekapType.BiayaMarketing
        else -> null
    }
}