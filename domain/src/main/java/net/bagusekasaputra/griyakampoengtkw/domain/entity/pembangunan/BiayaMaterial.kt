package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan

import net.bagusekasaputra.griyakampoengtkw.domain.IdUtil
import java.util.Calendar
import java.util.Date

data class BiayaMaterial(
    val keyId: String = IdUtil.generateKeyId(),
    private val untukKavling: String,
    val namaItem: String,
    val pcs: Int,
    val hargaPerItem: Long,
    private val tanggalBeli: Date,
    private val buktiPembayaran: String = "",
    private val mKeterangan: String = "-",
    private val mTimeMillis: Long = System.currentTimeMillis(),
): BiayaPembangunan(
    kavling = untukKavling,
    tanggal = tanggalBeli,
    biaya = pcs * hargaPerItem,
    keterangan = mKeterangan,
    timeMillis = mTimeMillis,
    uriFoto = buktiPembayaran,
) {
    val totalBiayaMaterial = biaya

    companion object {
        fun EMPTY(kavling: String): BiayaMaterial {
            return BiayaMaterial(
                untukKavling = kavling,
                namaItem = "-",
                pcs = 0,
                hargaPerItem = 0L,
                tanggalBeli = Calendar.getInstance().time,
            )
        }
    }
}