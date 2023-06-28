package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan

data class ProgressPembangunan(
    val kavling: String,
    val luas: Double,
    val hargaBorong: Long,
    val addendum: List<Addendum> = emptyList(),
    val persentaseRetensi: Double,
    val progress: Double,
    val danaTerserap: Long = 0L,
    val catatan: String = "",
) {
    val kontrakAwal: Double
        get() = luas * hargaBorong
    val danaTerserapSesuaiProgress: Double
        get() = progress * kontrakAwal


    data class Addendum(
        val jumlahUang: Long,
        val keterangan: String,
    ) {

        companion object {
            // TODO: hitung total
        }
    }
}