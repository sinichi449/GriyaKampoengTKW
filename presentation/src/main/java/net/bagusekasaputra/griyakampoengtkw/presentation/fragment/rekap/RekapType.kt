package net.bagusekasaputra.griyakampoengtkw.presentation.fragment.rekap

enum class RekapType {
    Besar,
    Global,
    UangMasuk,
    BiayaLain,
    FeeMarketing,
    BiayaMarketing,
    SisaPembayaran,
    User,
}

fun getRekapType(str: String): RekapType? {
    return when(str) {
        "Besar" -> RekapType.Besar
        "Global" -> RekapType.Global
        "UangMasuk" -> RekapType.UangMasuk
        "BiayaLain" -> RekapType.BiayaLain
        "FeeMarketing" -> RekapType.FeeMarketing
        "BiayaMarketing" -> RekapType.BiayaMarketing
        "SisaPembayaran" -> RekapType.SisaPembayaran
        "User" -> RekapType.User
        else -> null
    }
}