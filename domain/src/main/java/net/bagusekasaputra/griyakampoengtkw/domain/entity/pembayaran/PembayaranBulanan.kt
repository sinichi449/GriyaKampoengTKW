package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran

data class PembayaranBulanan(
    val kavling: String,
    val bulan: Int, // NOT  Calendar type of Bulan
    val tahun: Int,
    val listPembayaran: List<Pembayaran>,
    val baselinePembayaran: BaselinePembayaran,
) {
    val totalUangMasuk = Pembayaran.hitungTotalUangMasuk(listPembayaran)
    val totalTunggakan = baselinePembayaran.jumlahUang - totalUangMasuk

    val bulanStr = DateUtil.namaBulanShort(bulan)
    val parsedBulanTahun = "$bulanStr $tahun"

    companion object {
        fun sort(listPembayaranBulanan: List<PembayaranBulanan>): List<PembayaranBulanan> {
            return listPembayaranBulanan.sortedBy {
                // Convert bulan dan tahun ke objek Date, lalu diurut pakai "time" (timeMillis)
                val date = "1/${it.bulan}/${it.tahun}".toDate()

                date.time
            }
        }

        fun hitungSemuaTunggakan(pembayaranBulanans: List<PembayaranBulanan>): Long {
            var mTotal = 0L
            pembayaranBulanans.forEach {
                mTotal += it.totalTunggakan
            }

            return mTotal
        }
    }
}