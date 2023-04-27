package net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap

import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran

data class RekapBesarDetail(
    // Data Baru
    val mapListPembayaranRekapBaru: Map<String, List<PembayaranWithNamaCostumer>?>,
    val mapFeeMarketingRekapBaru: Map<String, FeeMarketing?>,
    val mapListBiayaMarketingRekapBaru: Map<String, List<BiayaMarketing>?>,

    // Data Lama
    val mapListPembayaranRekapLama: Map<String, List<PembayaranWithNamaCostumer>?> = mapOf(),
    val mapFeeMarketingRekapLama: Map<String, FeeMarketing?> = mapOf(),
    val mapListBiayaMarketingRekapLama: Map<String, List<BiayaMarketing>?> = mapOf(),

    // Agnostic :V
    val listBiayaLain: List<BiayaLain>?,
) {

    companion object {
        const val DATA_BARU = "DATA_BARU"
        const val DATA_LAMA = "DATA_LAMA"
    }

    fun getTotalUangMasukPembayaran(data: String): Long {
        val mapSelectedPembayaran = if (data == DATA_BARU)
            mapListPembayaranRekapBaru else mapListPembayaranRekapLama
        val mMapPembayaran = mutableMapOf<String, List<Pembayaran>?>()

        // Convert the map PembayaranWithNamaCostumer to Pembayaran
        mapSelectedPembayaran.keys.forEach { kavling ->
            val listPembayaran = mutableListOf<Pembayaran>()
            mapSelectedPembayaran[kavling]?.forEach { pembayaranWithNamaCostumer ->
                listPembayaran.add(pembayaranWithNamaCostumer.pembayaran)
            }

            if (listPembayaran.isNotEmpty()) {
                mMapPembayaran[kavling] = listPembayaran
            }
        }

        return Pembayaran.hitungTotalAllKavlingUangMasuk(mMapPembayaran)
    }

    fun getTotalFeeMarketing(data: String): Long {
        val mapFeeMarketing = if (data == DATA_BARU)
            mapFeeMarketingRekapBaru else mapFeeMarketingRekapLama

        return FeeMarketing.hitungTotalAllKavling(mapFeeMarketing)
    }
}