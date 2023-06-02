package net.bagusekasaputra.griyakampoengtkw.domain.entity.catatanPembayaran

abstract class CatatanPembayaran(
    val content: String
) {

    companion object {
        const val KAVLING = 0
        const val INDEN_BOOKING = 1
    }
}