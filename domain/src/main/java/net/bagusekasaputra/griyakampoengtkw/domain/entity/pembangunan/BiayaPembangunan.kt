package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan

import java.util.Date

abstract class BiayaPembangunan(
    val kavling: String,
    val tanggal: Date,
    val biaya: Long,
    val keterangan: String,
    val timeMillis: Long,
    val uriFoto: String,
) {

    companion object {
        fun List<BiayaPembangunan>.totalBiaya(): Long {
            var total = 0L
            this.forEach { item ->
                total += item.biaya
            }

            return total
        }
    }
}