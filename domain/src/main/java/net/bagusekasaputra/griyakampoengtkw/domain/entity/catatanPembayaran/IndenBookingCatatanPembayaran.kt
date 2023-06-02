package net.bagusekasaputra.griyakampoengtkw.domain.entity.catatanPembayaran

data class IndenBookingCatatanPembayaran(
    val keyId: String,
    private val mContent: String,
): CatatanPembayaran(mContent)