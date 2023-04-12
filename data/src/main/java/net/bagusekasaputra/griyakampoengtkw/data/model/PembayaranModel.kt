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

    companion object {
        fun pisahkanTerminDanUrutan(termin: String): Map<String, String> {
            val terminDanUrutan = termin.split(" ")
            return mapOf<String, String>(
                Pair("jenis", terminDanUrutan[0]),
                Pair("urutan", terminDanUrutan[1]),
            )
        }
    }
}