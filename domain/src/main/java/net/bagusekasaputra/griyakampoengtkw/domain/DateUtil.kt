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
        // Get first and end of day in current month
        val tanggalPertama = Calendar.getInstance().apply {
            // Set ke tanggal 1 bulan sekarang
            set(Calendar.DAY_OF_MONTH, 1)

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        val tanggalTerakhir = Calendar.getInstance().apply {
            // Set ke tanggal terakhir bulan sekarang (otomatis mengikuti bulan)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DATE))

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return listOf(tanggalPertama, tanggalTerakhir)
    }

    fun getMonthlyRangeDate(calendarMonth: Int, year: Int): List<Date> {
        val calendar = Calendar.getInstance()
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
        val startDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endDate = Calendar.getInstance().apply {
            time = startDate.time

            add(Calendar.DAY_OF_WEEK, 7)

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return listOf(startDate.time, endDate.time)
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
        val bulan = calendar.get(Calendar.MONTH) + 1
        val tahun = calendar.get(Calendar.YEAR)

        return "${tanggal}/${bulan}/${tahun}"
    }

    fun Date.isWithinRange(startDate: Date, endDate: Date)
            = !(this.before(startDate) || this.after(endDate))

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
}