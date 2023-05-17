package net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran

import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import java.math.BigDecimal
import java.util.Date

data class StatusPembayaran(
    val kavling: String,
    val listStatus: List<Status>,
) {
    val currentStatus = listStatus.last()

    sealed class Status(val tanggal: Date, val keterangan: String) {
        abstract fun getTitle(): String
    }

    data class Nil(
        val tanggalDibuka: Date,
        val keteranganNil: String = ""
    ): Status(tanggalDibuka, keteranganNil) {
        override fun getTitle(): String {
            return "Nil"
        }
    }

    data class Aktif(
        val tanggalItj: Date,
        val keteranganAktif: String = ""
    ): Status(tanggalItj, keteranganAktif) {
        override fun getTitle(): String {
            return "Aktif"
        }
    }

    data class Suspend(
        val tanggalSuspend: Date,
        val keteranganSuspend: String = "",
    ): Status(tanggalSuspend, keteranganSuspend) {
        override fun getTitle(): String {
            return "Suspend"
        }
    }

    data class Jeda(
        val tanggalJeda: Date,
        val keteranganJeda: String = ""
    ): Status(tanggalJeda, keteranganJeda) {
        override fun getTitle(): String {
            return "Jeda"
        }
    }

    data class Batal(
        val tanggalBatal: Date,
        val riwayatTotalUangMasuk: Long,
        val costumerPengganti: DataDiri,
        val keteranganBatal: String = "",
        var logsPengembalian: List<LogPengembalian> = emptyList(),
    ): Status(tanggalBatal, keteranganBatal) {

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