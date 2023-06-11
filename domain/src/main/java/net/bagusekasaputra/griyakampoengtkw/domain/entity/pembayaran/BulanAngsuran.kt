package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.normalize
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import java.util.Calendar
import java.util.Date

/**
 * Tanggal is set to Calendar.getActualMinimum(DAY_OF_MONTH)
 */
data class BulanAngsuran(
    // Not calendar type!!
    val bulan: Int,
    val tahun: Int,
): Comparator<BulanAngsuran> {
    val date: Date
        get() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, tahun)
        calendar.set(Calendar.MONTH, bulan - 1)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        calendar.normalize()

        return calendar.time
    }

    val bulanAndTahun: String get() {
        val bulanPadded = bulan.toString().padStart(2, '0')

        return "${bulanPadded}${DEFAULT_SEPARATOR}${tahun}"
    }

    override fun compare(first: BulanAngsuran, second: BulanAngsuran): Int {
        val item1 = first.date
        val item2 = second.date

        return item1.compareTo(item2)
    }

    companion object {
        const val DEFAULT_SEPARATOR = "/"

        // Parse tanggal pembayaran into Bulan Angsuran
        fun defaultToTanggalPembayaran(tanggalPembayaran: String): BulanAngsuran {
            val calendar = Calendar.getInstance().apply {
                time = tanggalPembayaran.toDate()
            }
            val bulan = calendar.get(Calendar.MONTH) + 1
            val tahun = calendar.get(Calendar.YEAR)

            return BulanAngsuran(bulan, tahun)
        }

        fun fromString(invoiceUntuk: String, separator: String): BulanAngsuran {
            val separateBulanAndTahun = invoiceUntuk.split(separator)

            return BulanAngsuran(
                bulan = separateBulanAndTahun[0].toInt(),
                tahun = separateBulanAndTahun[1].toInt(),
            )
        }

        fun getBulanSekarang(): BulanAngsuran {
            val calendar = Calendar.getInstance()
            val bulanIni = calendar.get(Calendar.MONTH) + 1
            val tahunIni = calendar.get(Calendar.YEAR)

            return BulanAngsuran(
                bulan = bulanIni,
                tahun = tahunIni,
            )
        }
    }

}