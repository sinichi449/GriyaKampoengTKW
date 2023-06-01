package net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap

import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran

/**
 * Special copy entity of Pembayaran to include DataDiri's Nama Costumer.
 * Useful for Rekap Uang Masuk Table
 */
data class PembayaranWithNamaCostumer(
    val kavling: String,
    val namaCostumer: String,
    val pembayaran: Pembayaran,
) {
    companion object {
        fun List<Pembayaran>.toListPembayaranWithNamaCostumer(kavling: String, namaCostumer: String): List<PembayaranWithNamaCostumer> {
            val list = mutableListOf<PembayaranWithNamaCostumer>()

            this.forEach {
                list.add(PembayaranWithNamaCostumer(
                    kavling = kavling,
                    namaCostumer = namaCostumer,
                    pembayaran = it,
                ))
            }

            return list
        }
    }
}