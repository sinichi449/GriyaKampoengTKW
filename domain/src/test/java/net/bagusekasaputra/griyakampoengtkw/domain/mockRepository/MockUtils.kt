package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import java.util.Calendar
import java.util.Date
import kotlin.random.Random

object MockUtils {

    fun getRandomDuwitValue(min: Long, max: Long) =
        Random.nextLong(from = min, until = max) * 10_000L

    fun getListDates(startDate: Date, endDate: Date): List<Date> {
        return mutableListOf<Date>().apply {
            val start = Calendar.getInstance().apply { time = startDate }
            val end = Calendar.getInstance().apply { time = endDate }

            while (start.compareTo(end) != 0) {
                val newDate = Calendar.getInstance().apply { time = start.time }

                add(newDate.time)

                start.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }
}