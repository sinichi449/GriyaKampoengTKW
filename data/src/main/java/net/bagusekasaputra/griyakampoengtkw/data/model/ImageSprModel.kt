package net.bagusekasaputra.griyakampoengtkw.data.model

data class ImageSprModel(
    val kavlingKode: String,
    val dstUri: String,
) {
    companion object {
        const val DST_ROOT = "spr_images"
    }

    fun getFilename() = "${kavlingKode}_SPR.png"

    fun getFullPath() = "${DST_ROOT}/${getFilename()}"
}