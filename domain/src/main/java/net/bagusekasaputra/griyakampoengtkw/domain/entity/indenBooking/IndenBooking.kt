package net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking

import android.net.Uri

/**
 * Only contains overview for showing in cards/anything dashboard-like
 */
data class IndenBooking(
    val namaCostumer: String,
    val noIdentitas: String,
    val totalUangMasuk: Long,
    val fotoIdentitas: Uri? = null,
    val keyId: String = "",
) {

    companion object {

        fun sortByName(indenBookings: List<IndenBooking>): List<IndenBooking> {
            return indenBookings.sortedBy {
                it.namaCostumer
            }
        }

    }
}