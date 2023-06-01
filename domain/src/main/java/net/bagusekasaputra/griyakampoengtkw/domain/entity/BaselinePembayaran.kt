package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toLocalDate
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Period
import java.util.Calendar
import java.util.Date

data class BaselinePembayaran(
    val kavling: String,
    val opsiBulan: Int,
    val jumlahUang: Long,
    val tanggalPembayaranMaks: Int,
    var timeMillis: Long = System.currentTimeMillis()
) {
    val parsedJumlahUang = NumberUtil.formatLongToString(jumlahUang)

    fun hitungSisaBlmBayarBulanIni(listPembayaran: List<Pembayaran>): Long {
        var totalPembayaranBulanIni = 0L

        listPembayaran.filterPeriode(PeriodeRekap.BULAN_INI, null, null)?.forEach {
            totalPembayaranBulanIni += it.parsedJumlahUangDibayar
        }

        // Sisa Belum Bayar = Angsuran Bulanan - Total Pembayaran Bulan Ini
        val sisaBelumBayarBulanIni = jumlahUang - totalPembayaranBulanIni

        if (sisaBelumBayarBulanIni < 0L) {
            return 0L
        }

        return sisaBelumBayarBulanIni
    }

    fun hitungSisaBulanAngsuran(sortedPembayarans: List<Pembayaran>): Int {
        val tanggalPembelian = Pembayaran.getTanggalPembelian(sortedPembayarans).toDate()

        return hitungSisaBulanAngsuran(tanggalPembelian)
    }

    fun hitungSisaBulanAngsuran(tanggalPembelian: Date): Int {
        // Sisa Bulan Angsuran = Opsi Bulan Angsuran - (Diff Bulan Sekarang & Bulan Tanggal Pembelian)
        val resetToTanggalSatuTanggalPembelian = Calendar.getInstance().apply {
            time = tanggalPembelian
            set(Calendar.DAY_OF_MONTH, 1)
        }.time
        val tanggalSekarang = Calendar.getInstance().time

        return opsiBulan - Period.between(resetToTanggalSatuTanggalPembelian.toLocalDate(), tanggalSekarang.toLocalDate())
            .months
    }

    companion object {
        fun hitungAngsuranPerBulan(hargaKavling: HargaKavling, timeframeBulan: Int): Double {
            // Tambah Luasan doesn't included
            val mHargaKavling = BigDecimal(hargaKavling.hargaLong)
            val mTimeFrameBulan = BigDecimal(timeframeBulan)

            val mAngsuranPerBulan = mHargaKavling.divide(mTimeFrameBulan, 2, RoundingMode.HALF_UP)

            return mAngsuranPerBulan.toDouble()
        }
    }
}