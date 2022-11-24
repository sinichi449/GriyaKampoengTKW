package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class ReportTotalUangMasuk(
    val kavling: String,
    val uangMasuk: Long,
    val feeMarketing: Long,
    val biayaMarketing: Long,
    val totalCuan: Long,
)