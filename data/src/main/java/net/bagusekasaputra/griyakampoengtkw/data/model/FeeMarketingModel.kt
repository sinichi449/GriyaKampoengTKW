package net.bagusekasaputra.griyakampoengtkw.data.model

data class FeeMarketingModel(
    var timeMillis: Long? = null,
    val kavlingKode: String = "",
    val namaMarketer: String = "",
    val biayaMarketer: Long = 0L,
)