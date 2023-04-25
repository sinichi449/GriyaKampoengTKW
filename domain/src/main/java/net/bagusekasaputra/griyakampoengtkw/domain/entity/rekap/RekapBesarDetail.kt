package net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap

import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran

data class RekapBesarDetail(
    val mapListPembayaranRekapBaru: Map<String, List<Pembayaran>?>,
    val mapFeeMarketingRekapBaru: Map<String, FeeMarketing?>,
    val mapListBiayaMarketingRekapBaru: Map<String, List<BiayaMarketing>?>,
    val mapListPembayaranRekapLama: Map<String, List<Pembayaran>?> = mapOf(),
    val mapFeeMarketingRekapLama: Map<String, FeeMarketing?>,
    val mapListBiayaMarketingRekapLama: Map<String, List<BiayaMarketing>?>,
    val listBiayaLain: List<BiayaLain>?,
) {

    fun getTotalUangMasukRekapBaru(): Long {
        return Pembayaran.hitungTotalAllKavlingUangMasuk(mapListPembayaranRekapBaru)
    }

    fun getTotalUangMasukRekapLama(): Long {
        return Pembayaran.hitungTotalAllKavlingUangMasuk(mapListPembayaranRekapLama)
    }
}