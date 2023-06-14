package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan

import net.bagusekasaputra.griyakampoengtkw.domain.IdUtil
import java.util.Date

data class UpahKerja(
    val keyId: String = IdUtil.generateKeyId(),
    private val untukKavling: String,
    val mandor: String,
    val hasilProgress: Double,
    val upah: Long,
    val mingguKe: Int,
    private val tanggalDibayar: Date,
    private val fotoBuktiPembayaran: String = "",
    private val mKeterangan: String = "-",
    private val mTimeMillis: Long = System.currentTimeMillis(),
): BiayaPembangunan(
    kavling = untukKavling,
    biaya = upah,
    tanggal = tanggalDibayar,
    keterangan = mKeterangan,
    timeMillis = mTimeMillis,
    uriFoto = fotoBuktiPembayaran,
)