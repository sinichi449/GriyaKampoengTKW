package net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran

import java.util.Date

data class LogPengembalian(
    val kavling: String,
    val tanggal: Date,
    val jumlahUangDikembalikan: Long,
    val keterangan: String = "",
) {

    companion object {
        fun hitungTotalUangSudahDikembalikan(logs: List<LogPengembalian>): Long {
            var mTotal = 0L

            logs.forEach {
                mTotal += it.jumlahUangDikembalikan
            }

            return mTotal
        }
    }
}