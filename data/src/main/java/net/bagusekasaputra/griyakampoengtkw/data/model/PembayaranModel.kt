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
        const val KEY_JENIS_TERMIN = "jenis"
        const val KEY_URUTAN_TERMIN = "urutan"

        fun pisahkanTerminDanUrutan(termin: String): Map<String, String> {
            val terminDanUrutan = termin.split(" ")
            return mapOf(
                Pair(KEY_JENIS_TERMIN, terminDanUrutan[0]),
                Pair(KEY_URUTAN_TERMIN, terminDanUrutan[1]),
            )
        }
    }
}