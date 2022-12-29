package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import java.util.*

data class FeeMarketing(
    val kavlingKode: String,
    val namaMarketer: String,
    var biayaMarketer: String,
    var tanggalPenerimaan: String,
) {
    val parsedBiayaMarketer = NumberUtil.formatStringToLong(biayaMarketer)

    fun getTimemillisTanggalPenerimaan(): Long {
        val formatToCalendar = tanggalPenerimaan.split("/").let {
            val tanggal = it[0].toInt()
            val bulan = it[1].toInt() - 1 // the index of calendar, I assume, is starting from 0 for January
            val tahun = it[2].toInt()

            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, tanggal)
                set(Calendar.MONTH, bulan)
                set(Calendar.YEAR, tahun)
            }
        }

        return formatToCalendar.timeInMillis
    }
}