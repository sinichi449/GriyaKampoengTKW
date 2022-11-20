package net.bagusekasaputra.griyakampoengtkw.data.model

data class PembayaranModel(
    val termin: String = "",
    val urutan: Int = 0,
    val tanggal: String = "",
    val jumlahUangDibayar: Long = 0L,
    val keterangan: String = "",
    val timeMillis: Long = 0L,
) {
    fun getFullTermin()
        = "$termin $urutan"
}