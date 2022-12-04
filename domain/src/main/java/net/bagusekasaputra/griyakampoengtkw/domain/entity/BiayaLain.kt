package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class BiayaLain(
    val id: Long? = null,
    val jenisBiaya: String,
    val harga: Long,
    val tanggal: String,
) {

    companion object {
        fun hitungTotalBiayaLain(listBiayaLain: List<BiayaLain>?): Long {
            var totalBiayaLain = 0L

            listBiayaLain?.forEach {
                totalBiayaLain += it.harga
            }

            return totalBiayaLain
        }
    }
}