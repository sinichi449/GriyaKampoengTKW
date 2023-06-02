package net.bagusekasaputra.griyakampoengtkw.domain.entity

abstract class AmbilKuitansi(
    val termin: String,
    val sudahAmbil: Boolean,
) {
    companion object {
        const val STANDARD = 0
        const val INDEN_BOOKING = 1
    }

}

data class StandardAmbilKuitansi(
    val kavling: String,
    private val mTermin: String,
    private val mSudahAmbil: Boolean,
): AmbilKuitansi(mTermin, mSudahAmbil)

data class IndenBookingAmbilKuitansi(
    val keyId: String,
    private val mTermin: String,
    private val mSudahAmbil: Boolean,
): AmbilKuitansi(mTermin, mSudahAmbil)