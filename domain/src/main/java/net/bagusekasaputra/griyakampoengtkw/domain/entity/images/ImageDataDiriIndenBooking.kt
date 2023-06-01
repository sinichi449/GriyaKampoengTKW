package net.bagusekasaputra.griyakampoengtkw.domain.entity.images

data class ImageDataDiriIndenBooking(
    val keyId: String,
    private val uriStrDataDiri: String,
): GktImage(uriStrDataDiri) {

    override fun getFilename(): String {
        return "${keyId}.png"
    }

    override fun getFolderPath(): String {
        return "inden_booking_images/data_diri_images"
    }

}