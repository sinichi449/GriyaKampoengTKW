package net.bagusekasaputra.griyakampoengtkw.domain

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberUtil {

    fun formatDoubleToString(double: Double): String {
        val decimalFormat = DecimalFormat("###,###,###.##", DecimalFormatSymbols(Locale.US))
        return decimalFormat.format(double)
    }

    fun formatStringToDouble(string: String): Double {
        return string.replace(",", "").toDouble()
    }

    fun formatLongToString(number: Long): String {
        val decimalFormat = DecimalFormat("###,###,###", DecimalFormatSymbols(Locale.US))
        return decimalFormat.format(number)
    }

    fun formatStringToLong(numStr: String): Long {
        return numStr.replace(",", "").toLong()
    }

    /**
     * Extension for [formatLongToString]
     */
    fun Long.numericToString(): String {
        return formatLongToString(this)
    }

    /**
     * Extension for [formatStringToLong]
     */
    fun String.numericToLong(): Long {
        return formatStringToLong(this)
    }

}