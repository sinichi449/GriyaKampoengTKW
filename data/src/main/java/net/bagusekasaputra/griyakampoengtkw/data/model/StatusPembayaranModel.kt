package net.bagusekasaputra.griyakampoengtkw.data.model

data class StatusPembayaranModel(
    val kavling: String = "",
    val listLogStatuses: List<List<LogStatusModel>> = emptyList(),
) {
    data class LogStatusModel(
        val namaStatus: String = "",
        val tanggal: String = "",
        val keterangan: String = "",
        val riwayatTotalUangMasuk: Long = 0L,
        val logPengembalians: List<LogPengembalianModel> = emptyList(),
    )

    data class LogPengembalianModel(
        val kavling: String = "",
        val tanggal: String = "",
        val jumlahUangDikembalikan: Long = 0L,
        val keterangan: String = "",
    )
}
