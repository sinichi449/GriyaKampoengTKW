package net.bagusekasaputra.griyakampoengtkw.domain.entity.catatanPembayaran

data class KavlingCatatanPembayaran(
    val kavlingKode: String,
    private val mContent: String,
): CatatanPembayaran(mContent)