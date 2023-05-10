package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import java.util.Calendar

data class PembayaranBulanan(
    val kavling: String,
    val bulan: Int, // NOT  Calendar type of Bulan
    val tahun: Int,
    val listPembayaran: List<Pembayaran>,
) {
    val total = Pembayaran.hitungTotalUangMasuk(listPembayaran)
    val bulanStr = DateUtil.namaBulanShort(bulan)
    val parsedBulanTahun = "$bulanStr-$tahun"

    fun jumlahUangTunggakan(baselinePembayaran: BaselinePembayaran): Long {
        return baselinePembayaran.jumlahUang - total
    }

    companion object {
        fun sort(listPembayaranBulanan: List<PembayaranBulanan>): List<PembayaranBulanan> {
            return listPembayaranBulanan.sortedBy {
                // Convert bulan dan tahun ke objek Date, lalu diurut pakai "time" (timeMillis)
                val date = "1/${it.bulan}/${it.tahun}".toDate()

                date.time
            }
        }
    }
}