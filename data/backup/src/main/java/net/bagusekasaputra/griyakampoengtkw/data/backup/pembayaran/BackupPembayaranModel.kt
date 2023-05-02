package net.bagusekasaputra.griyakampoengtkw.data.backup.pembayaran

data class BackupPembayaranModel(
    val kavling: String,
    val listPembayaran: List<Pembayaran>,
) {
    data class Pembayaran(
        val jumlahUangDibayar: Long,
        val keterangan: String,
        val tanggal: String,
        val termin: String,
        val timeMillis: Long,
        val urutan: Int,
    )
}