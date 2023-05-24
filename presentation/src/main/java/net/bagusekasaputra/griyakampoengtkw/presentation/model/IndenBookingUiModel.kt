package net.bagusekasaputra.griyakampoengtkw.presentation.model

import android.net.Uri

/**
 * Object for simplifying recycler view appearance Inden Booking
 */
data class IndenBookingUiModel(
    val namaCostumer: String,
    val noIdentitas: String,
    val totalUangMasuk: Long,
    val fotoIdentitas: Uri? = null,
) {

    companion object {
        fun getDummyModels(): List<IndenBookingUiModel> {
            val noIdentitas = "3507043008040003"

            return listOf(
                IndenBookingUiModel("Angga Nur Fatah", noIdentitas, 7_600_000L),
                IndenBookingUiModel("Kamila Mayasari", noIdentitas, 7_200_000L),
                IndenBookingUiModel("Emboh Gunawan", noIdentitas, 59_000_000L),
                IndenBookingUiModel("Supardi Warsito", noIdentitas, 1_000_000L),
                IndenBookingUiModel("Angga Nur Fatah", noIdentitas, 7_600_000L),
                IndenBookingUiModel("Kamila Mayasari", noIdentitas, 7_200_000L),
                IndenBookingUiModel("Emboh Gunawan", noIdentitas, 59_000_000L),
                IndenBookingUiModel("Supardi Warsito", noIdentitas, 1_000_000L),
                IndenBookingUiModel("Angga Nur Fatah", noIdentitas, 7_600_000L),
                IndenBookingUiModel("Kamila Mayasari", noIdentitas, 7_200_000L),
                IndenBookingUiModel("Emboh Gunawan", noIdentitas, 59_000_000L),
                IndenBookingUiModel("Supardi Warsito", noIdentitas, 1_000_000L),
            )
        }
    }
}