package net.bagusekasaputra.griyakampoengtkw.data.model

data class MaterialPembangunanModel(
    val keyId: String = "",
    val untuk: String = "",
    val kategori: String = "",
    val namaMaterial: String = "",
    val tanggal: String = "01/01/1970",
    val orderQty: Double = 0.0,
    val datangQty: Double = 0.0,
    val satuan: String = "",
    val hargaTotal: Long = 0L,
    val terbayar: Long = 0L,
    val keterangan: String = "",
)