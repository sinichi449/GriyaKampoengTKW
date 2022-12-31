package net.bagusekasaputra.griyakampoengtkw.domain.entity

import java.util.*

data class RekapUangMasuk(
    val noKavling: String,
    val namaCostumer: String,
    val tanggal: String,
    val jenisPembayaran: String,
    val jumlahPembayaran: Long,
) {
     companion object {
         fun hitungTotal(listRekap: List<RekapUangMasuk>?): Long {
             var total = 0L

             listRekap?.forEach {
                 total += it.jumlahPembayaran
             }

             return total
         }

         fun sortByKavlingAsc(listRekap: List<RekapUangMasuk>?): List<RekapUangMasuk>? {
             return listRekap?.sortedWith { rekap1, rekap2 ->
                 val kode1 = rekap1.noKavling[0]
                 val kode2 = rekap2.noKavling[0]

                 val nomor1 = rekap1.noKavling.substring(1).toInt()
                 val nomor2 = rekap2.noKavling.substring(1).toInt()

                 (kode1.compareTo(kode2)) or (nomor1.compareTo(nomor2))
             }
         }

         fun sortByJumlahPembayaranDesc(listRekap: List<RekapUangMasuk>?): List<RekapUangMasuk>? {
             return listRekap?.sortedByDescending {
                 it.jumlahPembayaran
             }
         }

         fun sortByTanggalAsc(listRekap: List<RekapUangMasuk>?): List<RekapUangMasuk>? {
             return listRekap?.sortedBy {
                 val splitTanggal = it.tanggal.split("/")
                 val tanggal = splitTanggal[0].toInt()
                 val bulan = splitTanggal[1].toInt() - 1
                 val tahun = splitTanggal[2].toInt()

                 Calendar.getInstance().apply {
                     set(Calendar.DAY_OF_MONTH, tanggal)
                     set(Calendar.MONTH, bulan)
                     set(Calendar.YEAR, tahun)
                     set(Calendar.HOUR_OF_DAY, 0)
                     set(Calendar.MINUTE, 0)
                     set(Calendar.SECOND, 0)
                     set(Calendar.MILLISECOND, 0)
                 }.timeInMillis
             }
         }
     }
}