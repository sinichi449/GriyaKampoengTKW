package net.bagusekasaputra.griyakampoengtkw.domain.entity

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
        fun getDummyModels(): List<IndenBooking> {
            val noIdentitas = "3507043008040003"

            return listOf(
                IndenBooking("Angga Nur Fatah", noIdentitas, 7_600_000L),
                IndenBooking("Kamila Mayasari", noIdentitas, 7_200_000L),
                IndenBooking("Emboh Gunawan", noIdentitas, 59_000_000L),
                IndenBooking("Supardi Warsito", noIdentitas, 1_000_000L),
                IndenBooking("Angga Nur Fatah", noIdentitas, 7_600_000L),
                IndenBooking("Kamila Mayasari", noIdentitas, 7_200_000L),
                IndenBooking("Emboh Gunawan", noIdentitas, 59_000_000L),
                IndenBooking("Supardi Warsito", noIdentitas, 1_000_000L),
                IndenBooking("Angga Nur Fatah", noIdentitas, 7_600_000L),
                IndenBooking("Kamila Mayasari", noIdentitas, 7_200_000L),
                IndenBooking("Emboh Gunawan", noIdentitas, 59_000_000L),
                IndenBooking("Supardi Warsito", noIdentitas, 1_000_000L),
            )
        }
    }
}