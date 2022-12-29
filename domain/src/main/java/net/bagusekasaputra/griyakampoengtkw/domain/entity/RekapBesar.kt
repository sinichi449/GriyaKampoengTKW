package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil

data class RekapBesar(
    val totalUangMasuk: Long,
    val totalSisaBelumBayar: Long,
    val totalFeeMarketing: Long,
    val totalBiayaMarketing: Long,
    val totalBiayaLain: Long,
) {
    val totalPengeluaran = totalFeeMarketing + totalBiayaMarketing + totalBiayaLain
    val sisaUang = totalUangMasuk - totalPengeluaran

    val parsedTotalUangMasuk = NumberUtil.formatLongToString(totalUangMasuk)
    val parsedTotalSisaBelumBayar = NumberUtil.formatLongToString(totalSisaBelumBayar)
    val parsedTotalFeeMarketing = NumberUtil.formatLongToString(totalFeeMarketing)
    val parsedTotalBiayaMarketing = NumberUtil.formatLongToString(totalBiayaMarketing)
    val parsedTotalBiayaLain = NumberUtil.formatLongToString(totalBiayaLain)
    val parsedTotalPengeluaran = NumberUtil.formatLongToString(totalPengeluaran)
    val parsedSisaUang = NumberUtil.formatLongToString(sisaUang)
}