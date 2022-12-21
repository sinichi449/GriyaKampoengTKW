package net.bagusekasaputra.griyakampoengtkw.data.model

data class ImageDataDiriModel(
    val kavlingKode: String = "",
    val imgUri: String = "",
) {

    companion object {
        const val DST_FOLDER = "data_diri_images"
    }

    fun getFilename() = "${kavlingKode}_data_diri.png"
    fun getFullPath() = "${DST_FOLDER}/${getFilename()}"
}