package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.catatanPembayaran

data class IndenBookingCatatanPembayaran(
    val keyId: String,
    val mContent: String,
): CatatanPembayaran(mContent)