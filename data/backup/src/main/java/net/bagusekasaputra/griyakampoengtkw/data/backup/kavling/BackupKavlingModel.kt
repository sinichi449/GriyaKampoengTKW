package net.bagusekasaputra.griyakampoengtkw.data.backup.kavling

data class BackupKavlingModel(
    val blok: String,
    val listKavling: List<Kavling>
) {
    data class Kavling(
        val active: Boolean,
        val kode: String,
        val type: String,
        val ukuran: String,
        val warna: String,
    )
}