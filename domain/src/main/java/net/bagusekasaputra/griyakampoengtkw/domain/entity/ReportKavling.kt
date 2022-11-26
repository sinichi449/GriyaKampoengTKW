package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil

data class ReportKavling(
    val kavling: String,
    val listPembayaran: List<Pembayaran>?,
    val feeMarketing: FeeMarketing?,
    val listBiayaMarketing: List<BiayaMarketing>?,
) {

    fun getTotalCuan(): Long {
        val totalPembayaran = getTotalPembayaran()
        val uangFeeMarketer = NumberUtil.formatStringToLong(feeMarketing?.biayaMarketer ?: "0")
        val totalBiayaMarketing = getTotalBiayaMarketing()

        return totalPembayaran - uangFeeMarketer - totalBiayaMarketing
    }

    fun getTotalPembayaran(): Long {
        var totalPembayaran = 0L

        listPembayaran?.forEach {
            totalPembayaran += NumberUtil.formatStringToLong(it.jumlahUangDibayar)
        }

        return totalPembayaran
    }

    fun getTotalBiayaMarketing(): Long {
        var totalBiayaMarketing = 0L

        listBiayaMarketing?.forEach {
            totalBiayaMarketing += NumberUtil.formatStringToLong(it.harga)
        }

        return totalBiayaMarketing
    }
}