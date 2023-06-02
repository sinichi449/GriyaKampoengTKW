package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.catatanPembayaran

data class KavlingCatatanPembayaran(
    val kavlingKode: String,
    private val mContent: String,
): CatatanPembayaran(mContent)