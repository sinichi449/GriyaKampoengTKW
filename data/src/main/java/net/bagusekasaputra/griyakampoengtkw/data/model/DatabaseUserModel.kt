package net.bagusekasaputra.griyakampoengtkw.data.model


data class DatabaseUserModel(
    val nama: String = "",
    val tanggal: String = "",
    val noHp: String = "",
    val usernameTiktok: String = "", // No "@" symbol
    val lokasiIndo: String = "",
    val negaraBekerja: String = "",
    val keterangan: String = "",
    var lastModified: Long = 0L,
)
