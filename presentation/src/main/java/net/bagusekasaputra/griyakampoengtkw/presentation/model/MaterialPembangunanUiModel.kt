package net.bagusekasaputra.griyakampoengtkw.presentation.model

data class MaterialPembangunanUiModel(
    val nama: String,
    val qty: Double,
    val satuan: String,
    val totalHarga: Long,
) {
    companion object {
        fun empty(): MaterialPembangunanUiModel {
            return MaterialPembangunanUiModel(
                nama = "-",
                qty = 0.0,
                satuan = "-",
                totalHarga = 0L,
            )
        }
    }
}