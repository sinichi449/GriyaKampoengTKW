package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan

import net.bagusekasaputra.griyakampoengtkw.domain.IdUtil
import net.bagusekasaputra.griyakampoengtkw.domain.interfaces.ContainImage
import java.util.Date

data class MaterialPembangunan(
    val keyId: String = IdUtil.generateUUID(),
    val untuk: String,
    val kategori: Kategori,
    val namaMaterial: String,
    val tanggal: Date,
    val qty: Double,
    val satuan: String,
    val hargaTotal: Long,
    val kelunasan: Kelunasan = Kelunasan.Lunas(hargaTotal),
    val kedatangan: Kedatangan = Kedatangan.Belum,
    val keterangan: String = "-",
    override val imageUris: List<String> = emptyList(),
): ContainImage {
    val hargaSatuan: Long get() = if (qty > 0.0) {
        (hargaTotal / qty).toLong()
    } else {
        0L
    }

    fun validate() {
        if (untuk.isEmpty()) throw IllegalArgumentException("Kavling belum terspesifikasi!")

        if (namaMaterial.isEmpty()) throw IllegalArgumentException("Nama material tidak boleh kosong!")

        if (qty < 0.0) throw IllegalArgumentException("Qty tidak boleh kurang dari nol")

        if (satuan.isEmpty()) throw IllegalArgumentException("Satuan tidak boleh kosong!")
    }

    fun getIdentifier(): Identifier {
        return Identifier(
            kategori = kategori,
            target = untuk,
            keyId = keyId,
        )
    }



    enum class Kategori {
        GLOBAL, KAVLING
    }

    sealed class Kelunasan(val terbayar: Long) {
        data class Lunas(val total: Long): Kelunasan(total) {
            fun getKembalian(totalHarga: Long): Long {
                return total - totalHarga
            }
        }

        object Belum: Kelunasan(0L)

        data class Partial(val jumlah: Long): Kelunasan(jumlah)
    }

    sealed class Kedatangan(val qty: Double) {
        data class Datang(val total: Double): Kedatangan(total)

        object Belum: Kedatangan(0.0)

        data class Partial(val jumlah: Double): Kedatangan(jumlah)
    }

    data class Identifier(
        val kategori: Kategori,
        val target: String,
        val keyId: String,
    )

    companion object {
        fun List<MaterialPembangunan>.totalBiaya(): Long {
            return this.sumOf { it.hargaTotal }
        }

        fun List<MaterialPembangunan>.sortByTanggal(): List<MaterialPembangunan> {
            return this.sortedBy { it.tanggal.time }
        }

        fun getKelunasan(jumlahTerbayar: Long, totalHarga: Long): Kelunasan {
            return if (jumlahTerbayar < totalHarga) {
                if (jumlahTerbayar == 0L) {
                    Kelunasan.Belum
                } else {
                    Kelunasan.Partial(jumlahTerbayar)
                }
            } else {
                Kelunasan.Lunas(jumlahTerbayar)
            }
        }

        fun getKedatangan(datangQty: Double, orderQty: Double): Kedatangan {
            return if (datangQty < orderQty) {
                if (datangQty == 0.0) {
                    Kedatangan.Belum
                } else {
                    Kedatangan.Partial(datangQty)
                }
            } else {
                Kedatangan.Datang(datangQty)
            }
        }

        fun getKategori(kategori: String): Kategori {
            return when (kategori.uppercase()) {
                "KAVLING" -> Kategori.KAVLING
                "GLOBAL" -> Kategori.GLOBAL
                else -> throw IllegalArgumentException("Kategori Material Pembangunan $kategori doesn't exist!")
            }
        }
    }
}