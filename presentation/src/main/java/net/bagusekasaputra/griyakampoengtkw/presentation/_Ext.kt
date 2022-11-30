package net.bagusekasaputra.griyakampoengtkw.presentation

import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

fun logEvent(msg: String) {
    Log.d(GriyaNodes.LOG_TAG, msg)
}

/**
 * Helper extension function to reset any Calendar's hour to 00:00:00
 */
fun Calendar.resetHours(): Calendar {
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)

    return this
}

fun String.toDate(): Date {
    val tanggal = this.split("/")[0].toInt()
    val bulan = this.split("/")[1].toInt() - 1
    val tahun = this.split("/")[2].toInt()

    val calendar = Calendar.getInstance().apply {
        set(tahun, bulan, tanggal)
    }.resetHours()

    return calendar.time
}

fun String.toCalendar(): Calendar {
    val tanggal = this.split("/")[0].toInt()
    val bulan = this.split("/")[1].toInt() - 1
    val tahun = this.split("/")[2].toInt()

    return Calendar.getInstance().apply {
        set(tahun, bulan, tanggal)
    }.resetHours()
}

fun Date.toSlashedDate(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    return formatter.format(this)
}

fun String.toHour(): Calendar {
    val splitHour = this.split(":")
    val hour = splitHour[0].toInt()
    val minute = splitHour[1].toInt()

    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

fun Double.juta(): Long {
    val bigDecimal = BigDecimal(this)
    val juta = BigDecimal(10.0.pow(6.0))
    return bigDecimal.multiply(juta).toLong()
}