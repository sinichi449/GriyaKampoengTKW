package net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran

import java.math.BigDecimal
import java.util.Date

data class StatusPembayaran(
    val kavling: String,
    val listLogStatuses: List<List<LogStatus>>,
) {
    val currentStatus = listLogStatuses.last().last()

    sealed class LogStatus(val tanggal: Date, val keterangan: String) {
        abstract fun getTitle(): String
    }

    data class Nil(
        val tanggalDibuka: Date,
        val keteranganNil: String = ""
    ): LogStatus(tanggalDibuka, keteranganNil) {
        override fun getTitle(): String {
            return "Nil"
        }
    }

    data class Aktif(
        val tanggalItj: Date,
        val keteranganAktif: String = ""
    ): LogStatus(tanggalItj, keteranganAktif) {
        override fun getTitle(): String {
            return "Aktif"
        }
    }

    data class Suspend(
        val tanggalSuspend: Date,
        val keteranganSuspend: String = "",
    ): LogStatus(tanggalSuspend, keteranganSuspend) {
        override fun getTitle(): String {
            return "Suspend"
        }
    }

    data class Jeda(
        val tanggalJeda: Date,
        val keteranganJeda: String = ""
    ): LogStatus(tanggalJeda, keteranganJeda) {
        override fun getTitle(): String {
            return "Jeda"
        }
    }

    data class Batal(
        val tanggalBatal: Date,
        val riwayatTotalUangMasuk: Long,
        val keteranganBatal: String = "",
        var logPengembalians: List<LogPengembalian> = emptyList(),
    ): LogStatus(tanggalBatal, keteranganBatal) {

        val uangTotalPengembalian: Long
            get() {
                val mRiwayatTotalUangMasuk = BigDecimal(riwayatTotalUangMasuk)
                val mPersentasePengembalian = BigDecimal(0.3) // 30%

                return mPersentasePengembalian.multiply(mRiwayatTotalUangMasuk)
                    .toLong()
            }

        override fun getTitle(): String {
            return "Batal"
        }
    }
}