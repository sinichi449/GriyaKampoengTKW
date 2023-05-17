package net.bagusekasaputra.griyakampoengtkw.data.model

data class StatusPembayaranModel(
    val kavling: String = "",
    val listStatus: List<StatusModel> = emptyList(),
) {
    data class StatusModel(
        val namaStatus: String = "",
        val tanggal: String = "",
        val riwayatTotalUangMasuk: Long = 0L,
        val namaCostumerPengganti: String = "",
        val logPengembalians: List<LogPengembalianModel> = emptyList(),
    )

    data class LogPengembalianModel(
        val kavling: String = "",
        val tanggal: String = "",
        val jumlahUangDikembalikan: Long = 0L,
    )
}
