package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan

import java.util.Date

abstract class BiayaPembangunan(
    val kavling: String,
    val tanggal: Date,
    val biaya: Long,
    val keterangan: String,
    val timeMillis: Long,
    val uriFoto: String,
)