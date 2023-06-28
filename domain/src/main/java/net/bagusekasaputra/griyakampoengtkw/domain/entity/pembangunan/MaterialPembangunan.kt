package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan

import net.bagusekasaputra.griyakampoengtkw.domain.IdUtil
import java.util.Date

data class MaterialPembangunan(
    val keyId: String = IdUtil.generateUUID(),
    val untukKavling: String,
    val namaMaterial: String,
    val tanggal: Date,
    val qty: Double,
    val satuan: String,
    val hargaTotal: Long,
    val kelunasan: Kelunasan = Kelunasan.Lunas(hargaTotal),
    val kedatangan: Kedatangan = Kedatangan.Belum,
    val keterangan: String = "",
) {
    val hargaSatuan: Long get() = if (qty > 0.0) {
        (hargaTotal / qty).toLong()
    } else {
        0L
    }

    sealed class Kelunasan(val terbayar: Long) {
        data class Lunas(val total: Long): Kelunasan(total)

        object Belum: Kelunasan(0L)

        data class Partial(val jumlah: Long): Kelunasan(jumlah)
    }

    sealed class Kedatangan(val qty: Double) {
        data class Datang(val total: Double): Kedatangan(total)

        object Belum: Kedatangan(0.0)

        data class Partial(val jumlah: Double): Kedatangan(jumlah)
    }

    companion object {

        fun List<MaterialPembangunan>.totalBiaya(): Long {
            return this.sumOf { it.hargaTotal }
        }

    }
}