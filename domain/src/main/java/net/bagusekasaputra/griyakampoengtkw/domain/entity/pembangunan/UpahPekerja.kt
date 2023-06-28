package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan

import net.bagusekasaputra.griyakampoengtkw.domain.IdUtil
import java.util.Date

data class UpahPekerja(
    val keyId: String = IdUtil.generateUUID(),
    val untukKavling: String,
    val tanggalDibayarkan: Date,
    val mingguKe: Int,
    val mandor: String,
    val progress: Double,
    val jumlahDibayarkan: Long,
    val keterangan: String = "-",
    val kuitansiUri: String = "",
) {
    companion object {

        fun List<UpahPekerja>.totalDibayarkan(): Long {
            return this.sumOf { it.jumlahDibayarkan }
        }

    }
}