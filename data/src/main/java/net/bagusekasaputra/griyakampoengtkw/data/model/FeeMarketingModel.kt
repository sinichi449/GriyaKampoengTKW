package net.bagusekasaputra.griyakampoengtkw.data.model

import java.text.SimpleDateFormat
import java.util.*

data class FeeMarketingModel(
    var timeMillis: Long? = null,
    val kavlingKode: String = "",
    val namaMarketer: String = "",
    val biayaMarketer: Long = 0L,
) {

    // The remote repository provide tanggal in TimeMillis, but the UI need it in
    // standard date format, so here mapping.
    fun getTanggalStr(): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMillis ?: 0L
        }
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)

        return dateFormatter.format(calendar.time)
    }
}