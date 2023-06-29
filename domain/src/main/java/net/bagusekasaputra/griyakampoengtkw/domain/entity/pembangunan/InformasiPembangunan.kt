package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan

import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.InformasiPembangunan.Addendum.Companion.totalAddendum

data class InformasiPembangunan(
    val kavling: String,
    val luas: Double,
    val hargaBorong: Long,
    val addendum: List<Addendum> = emptyList(),
    val persentaseRetensi: Double,
    val progress: Double,
    val danaTerserap: Long = 0L,
    val catatan: String = "",
) {
    val kontrakAwal: Long
        get() = (luas * hargaBorong).toLong()
    val danaTerserapSesuaiProgress: Long
        get() = (progress * kontrakAwal).toLong()
    val besaranRetensi: Long
        get() = (danaTerserapSesuaiProgress * persentaseRetensi).toLong()
    val kontrakDanAddendum: Long
        get() = (kontrakAwal + addendum.totalAddendum())
    val bisaDiserap: Long
        get() = danaTerserapSesuaiProgress - danaTerserap - besaranRetensi


    data class Addendum(
        val jumlahUang: Long,
        val keterangan: String,
    ) {

        companion object {
            fun List<Addendum>.totalAddendum(): Long {
                return this.sumOf { it.jumlahUang }
            }
        }
    }

    companion object {
        fun EMPTY(kavling: String): InformasiPembangunan {
            return InformasiPembangunan(
                kavling = kavling,
                luas = 0.0,
                hargaBorong = 0L,
                persentaseRetensi = 0.0,
                progress = 0.0,
                danaTerserap = 0L,
            )
        }
    }
}