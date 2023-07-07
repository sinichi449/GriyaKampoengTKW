package net.bagusekasaputra.griyakampoengtkw.presentation.model.pembangunan

import net.bagusekasaputra.griyakampoengtkw.domain.IdUtil
import java.util.Date

data class PembelianLog(
    val keyId: String = IdUtil.generateUUID(),
    val tanggal: Date,
    val supplier: String,
    val orderQty: Double,
    val arrivedQty: Double,
    val harga: Long,
    val terbayar: Long,
) {
    val datangSemua = orderQty == arrivedQty
    val lunas = harga == terbayar
}