package net.bagusekasaputra.griyakampoengtkw.domain

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

object DateUtil {

    fun getTahunSekarang() = Calendar.getInstance().get(Calendar.YEAR)

    fun getYearlyRangeDate(): List<Date> {
        val tanggalPertama = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MONTH, Calendar.JANUARY)

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        val tanggalTerakhir = Calendar.getInstance().time

        return listOf(tanggalPertama, tanggalTerakhir)
    }

    fun getMonthlyRangeDate(): List<Date> {
        val calendar = Calendar.getInstance().normalize()

        val startDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, startDay)
        val startDate = calendar.time

        val endDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, endDay)
        val endDate = calendar.time

        return listOf(startDate, endDate)
    }

    fun getMonthlyRangeDate(calendarMonth: Int, year: Int): List<Date> {
        val calendar = Calendar.getInstance().normalize()
        calendar.set(Calendar.MONTH, calendarMonth)
        calendar.set(Calendar.YEAR, year)

        val startDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, startDay)
        val startDate = calendar.time

        val endDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, endDay)
        val endDate = calendar.time

        return listOf(startDate, endDate)
    }

    fun getWeeklyRangeDate(): List<Date> {
        val calendar = Calendar.getInstance().normalize()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val startDate = calendar.time

        calendar.add(Calendar.DAY_OF_WEEK, 7)
        val endDate = calendar.time

        return listOf(startDate, endDate)
    }

    fun getCustomRangeDate(start: Date, end: Date): List<Date> {
        val startDate = Calendar.getInstance().apply {
            time = start

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endDate = Calendar.getInstance().apply {
            time = end

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return listOf(startDate.time, endDate.time)
    }

    fun getListMonths(dateFrom: Date, dateTo: Date): List<Date> {
        val calendar = Calendar.getInstance().apply {
            time = dateFrom
            // Reset tanggal
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val months = mutableListOf<Date>()

        while (calendar.time.time <= dateTo.time) {
            months.add(calendar.time)
            calendar.add(Calendar.MONTH, 1)
        }

        return months
    }

    fun getListDate(dateFrom: Date, dateTo: Date): List<Date> {
        val calendar = Calendar.getInstance().apply {
            time = dateFrom
        }

        val days = mutableListOf<Date>()

        while (calendar.time.time <= dateTo.time) {
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
    }

    fun String.toDate(): Date {
        return this.split("/").let {
            val tanggal = it[0].toInt()
            val bulan = it[1].toInt() - 1
            val tahun = it[2].toInt()

            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, tanggal)
                set(Calendar.MONTH, bulan)
                set(Calendar.YEAR, tahun)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }
    }

    fun Date.toSlashedString(): String {
        val calendar = Calendar.getInstance().apply { time = this@toSlashedString }
        val tanggal = calendar.get(Calendar.DAY_OF_MONTH)
            .toString()
            .padStart(2, '0')
        val bulan = calendar.get(Calendar.MONTH).plus(1)
            .toString()
            .padStart(2, '0')
        val tahun = calendar.get(Calendar.YEAR)

        return "${tanggal}/${bulan}/${tahun}"
    }

    fun Date.toStringAndLongBulan(): String {
        val calendar = Calendar.getInstance().apply {
            time = this@toStringAndLongBulan
        }
        val tanggal = calendar.get(Calendar.DAY_OF_MONTH)
        val bulan = calendar.get(Calendar.MONTH) + 1
        val tahun = calendar.get(Calendar.YEAR)

        return "$tanggal ${namaBulanLong(bulan)} $tahun"
    }

    fun Date.isWithinRange(startDate: Date, endDate: Date)
            = !(this.before(startDate) || this.after(endDate))

    fun Calendar.normalize(): Calendar {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        return this
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Date.toLocalDate(): LocalDate {
        return this.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun LocalDate.toDate(): Date {
        return Date.from(this
            .atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()
        )
    }

    fun namaBulanShort(bulan: Int): String {
        return when (bulan) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "Mei"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Okt"
            11 -> "Nov"
            12 -> "Des"
            else -> throw IllegalArgumentException("Tidak ada nama bulan yang sesuai untuk Bulan $bulan")
        }
    }

    fun namaBulanLong(bulan: Int): String {
        return when (bulan) {
            1 -> "Januari"
            2 -> "Februari"
            3 -> "Maret"
            4 -> "April"
            5 -> "Mei"
            6 -> "Juni"
            7 -> "Juli"
            8 -> "Agustus"
            9 -> "September"
            10 -> "Oktober"
            11 -> "November"
            12 -> "Desember"
            else -> throw IllegalArgumentException("Tidak ada nama bulan yang sesuai untuk Bulan $bulan")
        }
    }

    fun bulanListBahasaIndo(): List<String> {
        val totalAvailableBulan = 12
        return buildList(totalAvailableBulan) {
            repeat(totalAvailableBulan) { index ->
                add(index, namaBulanLong(index + 1))
            }
        }.toList()
    }

    fun tahunListOf(last: Int = 5): List<String> {
        val tahunSekarang = Calendar.getInstance().get(Calendar.YEAR)

        return buildList(last) {
            repeat(last) { index ->
                val tahun = tahunSekarang - index

                add(tahun.toString())
            }
        }
    }
}