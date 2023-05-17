package net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran

import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import java.math.BigDecimal
import java.util.Date

data class StatusPembayaran(
    val kavling: String,
    val listStatus: List<Status>,
) {
    val currentStatus = listStatus.last()

    sealed class Status(val tanggal: Date)

    data class Nil(val tanggalDibuka: Date): Status(tanggalDibuka)

    data class Aktif(val tanggalItj: Date): Status(tanggalItj)

    data class Suspend(val tanggalSuspend: Date): Status(tanggalSuspend)

    data class Jeda(val tanggalJeda: Date): Status(tanggalJeda)

    data class Batal(
        val tanggalBatal: Date,
        val riwayatTotalUangMasuk: Long,
        val costumerPengganti: DataDiri,
        var logsPengembalian: List<LogPengembalian> = emptyList(),
    ): Status(tanggalBatal) {

        val uangTotalPengembalian: Long
            get() {
                val mRiwayatTotalUangMasuk = BigDecimal(riwayatTotalUangMasuk)
                val mPersentasePengembalian = BigDecimal(0.3) // 30%

                return mPersentasePengembalian.multiply(mRiwayatTotalUangMasuk)
                    .toLong()
            }
    }
}