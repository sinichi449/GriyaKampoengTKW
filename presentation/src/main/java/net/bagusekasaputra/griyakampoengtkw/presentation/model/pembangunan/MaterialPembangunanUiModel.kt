package net.bagusekasaputra.griyakampoengtkw.presentation.model.pembangunan

import net.bagusekasaputra.griyakampoengtkw.domain.IdUtil

data class MaterialPembangunanUiModel(
    val keyId: String = IdUtil.generateUUID(),
    val nama: String,
    val satuan: String,
    val logs: List<PembelianLog> = emptyList(),
) {
    val hargaTotal = logs.sumOf { it.harga }
    val qtyTotal = logs.sumOf { it.arrivedQty }

    companion object {
        fun empty(): MaterialPembangunanUiModel {
            return MaterialPembangunanUiModel(
                nama = "-",
                satuan = "-",
            )
        }
    }
}