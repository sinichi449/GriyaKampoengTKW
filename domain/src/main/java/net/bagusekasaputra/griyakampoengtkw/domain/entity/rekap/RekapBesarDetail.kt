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

    fun getTotalUangMasukRekapBaru(): Long {
        val mapPembayaran = mutableMapOf<String, List<Pembayaran>?>()

        mapListPembayaranRekapBaru.keys.forEach { kavling ->
            val listPembayaran = mutableListOf<Pembayaran>()
            mapListPembayaranRekapBaru[kavling]?.forEach { pembayaranWithNamaCostumer ->
                listPembayaran.add(pembayaranWithNamaCostumer.pembayaran)
            }

            if (listPembayaran.isNotEmpty()) {
                mapPembayaran[kavling] = listPembayaran
            }
        }

        return Pembayaran.hitungTotalAllKavlingUangMasuk(mapPembayaran)
    }

    fun getTotalUangMasukRekapLama(): Long {
        val mapPembayaran = mutableMapOf<String, List<Pembayaran>?>()

        mapListPembayaranRekapLama.keys.forEach { kavlingLama ->
            val listPembayaran = mutableListOf<Pembayaran>()
            mapListPembayaranRekapLama[kavlingLama]?.forEach { pembayaranWithNamaCostumer ->
                listPembayaran.add(pembayaranWithNamaCostumer.pembayaran)
            }

            if (listPembayaran.isNotEmpty()) {
                mapPembayaran[kavlingLama] = listPembayaran
            }
        }

        return Pembayaran.hitungTotalAllKavlingUangMasuk(mapPembayaran)
    }
}