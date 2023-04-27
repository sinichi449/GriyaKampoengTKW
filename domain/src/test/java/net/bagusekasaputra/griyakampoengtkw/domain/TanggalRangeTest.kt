package net.bagusekasaputra.griyakampoengtkw.domain

import net.bagusekasaputra.griyakampoengtkw.domain.mockRepository.MockUtils
import org.junit.Test
import java.util.Calendar
import java.util.Date

class TanggalRangeTest {

    @Test
    fun test_date_range() {
        val startDate = "01/01/2022".toDate()
        val endDate = "01/04/2022".toDate()

        val range = "01/03/2022".toDate()

        val listDate = MockUtils.getListDates(startDate, endDate)
        val filtered = listDate.filter {
            it >= range
        }
        filtered.forEach {
            println(it.toSlashedString())
        }
    }

    @Test
    fun test_get_last_date_of_month() {
        val randomDateDecember = Calendar.getInstance().apply {
            time = "22/02/2020".toDate()

            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DATE))
        }.time

        println(randomDateDecember.toSlashedString())
    }

    @Test
    fun test_monthly_range_date() {
        val thisShouldTrue = "03/03/2022".toDate()
        val thisShouldFalse = "02/04/2022".toDate()

        val startDate = "01/03/2022".toDate()
        val endDate = "31/03/2022".toDate()

        assert(thisShouldTrue.isWithinRange(startDate, endDate))
        assert(!thisShouldFalse.isWithinRange(startDate, endDate))
    }

    @Test
    fun test_weekly_range_date() {
        val startDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }
        val endDate = Calendar.getInstance().apply {
            time = startDate.time

            add(Calendar.DAY_OF_WEEK, 7)
        }

        println("Start date is at ${startDate.time.toSlashedString()} and the end is at ${endDate.time.toSlashedString()}")

        val listDate = listOf<String>(
            "22/12/2022",
            "23/12/2022",
            "24/12/2022",
            "25/12/2022",
            "26/12/2022",
            "27/12/2022",
            "28/12/2022",
            "29/12/2022",
            "30/12/2022",
            "31/12/2022",
            "01/01/2023",
            "02/01/2023",
            "03/01/2023"
        )

        listDate.forEach {
            println("$it    -> ${it.toDate().isWithinRange(startDate.time, endDate.time)}")
        }
    }

    private fun String.toDate(): Date {
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

    private fun Date.toSlashedString(): String {
        val calendar = Calendar.getInstance().apply { time = this@toSlashedString }
        val tanggal = calendar.get(Calendar.DAY_OF_MONTH)
        val bulan = calendar.get(Calendar.MONTH) + 1
        val tahun = calendar.get(Calendar.YEAR)

        return "${tanggal}/${bulan}/${tahun}"
    }

    private fun Date.isWithinRange(startDate: Date, endDate: Date)
            = !(this.before(startDate) || this.after(endDate))

}