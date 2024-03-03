package net.bagusekasaputra.griyakampoengtkw.data.model

data class PembayaranTambahLuasanModel(
    val kavling: String = "",
    val id: String = "",
    val sudahAmbilKuitansi: Boolean = false,
    val tanggal: String = "",
    val jumlahUang: Long = 0L,
    val keterangan: String = "",
    val timeMillis: Long = 0L,
) {
}