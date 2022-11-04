package net.bagusekasaputra.griyakampoengtkw.util

import java.text.DecimalFormat

object NumberUtil {

    fun formatLongToString(number: Long): String {
        val decimalFormat = DecimalFormat("#,###,###,###")
        return decimalFormat.format(number)
    }

    fun formatStringToLong(numStr: String): Long {
        val parsed = numStr.replace(",", "").toLong()

        return parsed
    }
}