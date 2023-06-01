package net.bagusekasaputra.griyakampoengtkw.domain.entity.images

data class FotoPembayaranIndenBooking(
    val keyId: String,
    val termin: String,
    private val uriStrFotoPembayaran: String, // Can't use direct uri because it can't be mocked for test.
): GktImage(uriStrFotoPembayaran) {

    override fun getFilename(): String {
        val replaceTerminSpaceWithUnderscore = termin.replace(" ", "_")
        return "$replaceTerminSpaceWithUnderscore--${keyId}.png"
    }

    override fun getFolderPath(): String {
        return "inden_booking_images/foto_pembayaran_images"
    }

}