package net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap

import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran

data class RekapBesarDetail(
    // Data Baru
    val pembayaranBaru: Map<String, List<PembayaranWithNamaCostumer>?>,
    val feeMarketingBaru: Map<String, FeeMarketing?>,
    val biayaMarketingBaru: Map<String, List<BiayaMarketing>?>,

    // Data Lama
    val pembayaranLama: Map<String, List<PembayaranWithNamaCostumer>?> = mapOf(),
    val feeMarketingLama: Map<String, FeeMarketing?> = mapOf(),
    val biayaMarketingLama: Map<String, List<BiayaMarketing>?> = mapOf(),

    // Agnostic :V
    val listBiayaLain: List<BiayaLain>?,
    val listSisaPembayaran: List<SisaPembayaran>,
) {

    companion object {
        const val DATA_BARU = "DATA_BARU"
        const val DATA_LAMA = "DATA_LAMA"
    }

    fun getTotalUangMasukPembayaran(data: String): Long {
        val mapSelectedPembayaran = if (data == DATA_BARU)
            pembayaranBaru else pembayaranLama
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

        return Pembayaran.hitungTotalAllKavling(mMapPembayaran)
    }

    fun getTotalFeeMarketing(data: String): Long {
        val mapFeeMarketing = if (data == DATA_BARU)
            feeMarketingBaru else feeMarketingLama

        return FeeMarketing.hitungTotalAllKavling(mapFeeMarketing)
    }

    fun getTotalBiayaMarketing(data: String): Long {
        val mapBiayaMarketing = if (data == DATA_BARU)
            biayaMarketingBaru else biayaMarketingLama

        return BiayaMarketing.hitungTotalAllKavling(mapBiayaMarketing)
    }
}