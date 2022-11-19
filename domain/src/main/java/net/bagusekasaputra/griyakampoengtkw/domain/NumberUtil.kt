package net.bagusekasaputra.griyakampoengtkw.domain

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object NumberUtil {

    fun formatLongToString(number: Long): String {
        val decimalFormat = DecimalFormat("###,###,###", DecimalFormatSymbols(Locale.US))
        return decimalFormat.format(number)
    }

    fun formatStringToLong(numStr: String): Long {
        return numStr.replace(",", "").toLong()
    }
}