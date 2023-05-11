package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getCustomRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getMonthlyRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getWeeklyRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.isWithinRange
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.PembayaranSorterUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar
import java.util.Date

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

    fun hitungPersentase(hargaKavling: HargaKavling): Double {
        val floatTotalUangMasuk = NumberUtil.formatStringToLong(totalUangMasuk).toFloat()
        val floatHargaKavling = hargaKavling.toFloat()
        val persentase = floatTotalUangMasuk.div(floatHargaKavling).let {
            val bigDecimal = it.toBigDecimal().setScale(4, RoundingMode.HALF_UP)
            return@let bigDecimal.multiply(BigDecimal.valueOf(100))
        }

        return persentase.toDouble()
    }

    companion object {
        fun hitungTotalUangMasuk(listPembayaran: List<Pembayaran>): Long {
            var mTotal = 0L

            listPembayaran.forEach {
                mTotal += NumberUtil.formatStringToLong(it.jumlahUangDibayar)
            }

            return mTotal
        }

        fun hitungTotalAllKavling(mapListPembayaran: Map<String, List<Pembayaran>?>): Long {
            var mTotal = 0L

            mapListPembayaran.keys.forEach { kavling ->
                mapListPembayaran[kavling]?.forEach { pembayaran ->
                    mTotal += pembayaran.parsedJumlahUangDibayar
                }
            }

            return mTotal
        }

        fun hitungTotalSisaBelumBayar(hargaKavling: HargaKavling, jumlahUangMasukKavling: Long): Long {
            return hargaKavling.hargaDanTambahLuasan - jumlahUangMasukKavling
        }

        fun getSisaBelumTerbayar(pembayarans: List<Pembayaran>): String {
            return pembayarans.last().sisaBelumTerbayar
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

        fun groupIntoBulanan(kavling: String, baselinePembayaran: BaselinePembayaran, sortedListPembayaran: List<Pembayaran>): List<PembayaranBulanan> {
            val listPembayaranBulanan = mutableListOf<PembayaranBulanan>()

            var listBulanTahun = mutableListOf<Pair<Int, Int>>()
            sortedListPembayaran.forEach { pembayaran ->
                val tanggalPembayaran = Calendar.getInstance().apply {
                    time = pembayaran.tanggal.toDate()
                }
                val bulanPembayaran = tanggalPembayaran.get(Calendar.MONTH)
                val tahunPembayaran = tanggalPembayaran.get(Calendar.YEAR)

                listBulanTahun.add(Pair(bulanPembayaran, tahunPembayaran))
            }
            listBulanTahun = listBulanTahun.distinct().toMutableList() // Filter out duplicate

            listBulanTahun.forEach { bulanTahun ->
                val rangeTanggal = DateUtil.getMonthlyRangeDate(bulanTahun.first, bulanTahun.second)
                val tanggalPertama = rangeTanggal[0]
                val tanggalTerakhir = rangeTanggal[1]
                val listOnlySpecifiedBulan = sortedListPembayaran.filterPeriode(PeriodeRekap.CUSTOM, tanggalPertama, tanggalTerakhir)

                if (listOnlySpecifiedBulan != null) {
                    listPembayaranBulanan.add(PembayaranBulanan(kavling,
                        bulanTahun.first.plus(1), // Bulan yang ada diisini pake formatnya Calendar, so harus +1
                        bulanTahun.second,
                        listOnlySpecifiedBulan,
                        baselinePembayaran,
                    ))
                }
            }

            return PembayaranBulanan.sort(listPembayaranBulanan)
        }

        suspend fun maskPembayaran(
            listPembayaran: List<Pembayaran>,
            hargaKavling: HargaKavling,
            onCekFotoPembayaran: suspend (termin: String) -> Boolean,
        ): List<Pembayaran> {
            val sortedListPembayaran = sortPembayaran(listPembayaran)
            val newListPembayaran = ArrayList<Pembayaran>()
            var totalUangMasuk = 0L

            sortedListPembayaran.forEach {
                totalUangMasuk += NumberUtil.formatStringToLong(it.jumlahUangDibayar)
                it.totalUangMasuk = NumberUtil.formatLongToString(totalUangMasuk)
                it.presentase = it.hitungPersentase(hargaKavling)
                it.sisaBelumTerbayar = NumberUtil.formatLongToString(hargaKavling - totalUangMasuk)
                it.sudahIsiFotoPembayaran = onCekFotoPembayaran(it.termin)

                newListPembayaran.add(it)
            }

            return newListPembayaran
        }
    }
}