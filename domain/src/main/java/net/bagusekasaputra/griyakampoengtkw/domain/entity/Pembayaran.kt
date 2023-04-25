package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getCustomRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getMonthlyRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getWeeklyRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.isWithinRange
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.PembayaranSorterUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import java.util.*

data class Pembayaran(
    val termin: String,
    val tanggal: String,
    val jumlahUangDibayar: String = "",
    var totalUangMasuk: String = "",
    var presentase: Double = 0.0,
    var sisaBelumTerbayar: String = "",
    val keterangan: String,
    // Timemillis is used as a Primary Key in the Room Database
    val timeMillis: Long,
    var sudahIsiFotoPembayaran: Boolean = false,
) {
    val parsedJumlahUangDibayar = NumberUtil.formatStringToLong(jumlahUangDibayar)

    fun getUrutan(): Int {
        return termin.split(" ")[1].toInt()
    }

    fun getJenisTermin(): String {
        return termin.split(" ")[0]
    }

    companion object {
        fun hitungTotalUangMasuk(listPembayaran: List<Pembayaran>): Long {
            var mTotal = 0L

            listPembayaran.forEach {
                mTotal += NumberUtil.formatStringToLong(it.jumlahUangDibayar)
            }

            return mTotal
        }

        fun hitungTotalSisaBelumBayar(hargaKavling: HargaKavling, jumlahUangMasukKavling: Long): Long {
            return hargaKavling.hargaDanTambahLuasan - jumlahUangMasukKavling
        }

        fun sortPembayaran(listPembayaran: List<Pembayaran>) = PembayaranSorterUtil(listPembayaran).getSortedList()

        fun getTanggalPembelian(sortedListPembayaran: List<Pembayaran>) =
            if (sortedListPembayaran.isEmpty())
                throw Exception("Get tanggal pembelian gagal -> argumen sortedListPembayaran dengan list masih kosong tidak boleh!")
            else
                sortedListPembayaran.first().tanggal

        fun List<Pembayaran>?.filterPeriode(
            periode: PeriodeRekap,
            start: Date?,
            end: Date?,
        ): List<Pembayaran>? {
            return when(periode) {
                PeriodeRekap.SEMUA -> this
                PeriodeRekap.TAHUN_INI ->  {
                    this?.filter { pembayaran ->
                        val tahunPembayaran = Calendar.getInstance().let {
                            it.time = pembayaran.tanggal.toDate()
                            it.get(Calendar.YEAR)
                        }

                        tahunPembayaran == Calendar.getInstance().get(Calendar.YEAR)
                    }
                }
                PeriodeRekap.BULAN_INI -> {
                    val rangeTanggal = getMonthlyRangeDate()
                    val tanggalPertama = rangeTanggal[0]
                    val tanggalTerakhir = rangeTanggal[1]

                    this?.filter { pembayaran ->
                        val tanggalPembayaran = pembayaran.tanggal.toDate()

                        tanggalPembayaran.isWithinRange(tanggalPertama, tanggalTerakhir)
                    }
                }
                PeriodeRekap.MINGGU_INI -> {
                    // Get first and end date of the week, which is get
                    // the date of Sunday and the next Sunday
                    val rangeTanggal = getWeeklyRangeDate()
                    val startDate = rangeTanggal[0]
                    val endDate = rangeTanggal[1]

                    this?.filter { pembayaran ->
                        val tanggalPembayaran = pembayaran.tanggal.toDate()

                        tanggalPembayaran.isWithinRange(startDate, endDate)
                    }

                }
                PeriodeRekap.CUSTOM -> {
                    val rangeTanggal = getCustomRangeDate(start!!, end!!)
                    val startDate = rangeTanggal[0]
                    val endDate = rangeTanggal[1]

                    this?.filter {
                        val tanggalPembayaran = it.tanggal.toDate()

                        tanggalPembayaran.isWithinRange(startDate, endDate)
                    }
                }
            }
        }
    }
}