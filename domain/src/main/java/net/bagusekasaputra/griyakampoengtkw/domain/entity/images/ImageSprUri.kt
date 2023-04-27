package net.bagusekasaputra.griyakampoengtkw.domain.entity.images

/**
 * Difference with ImageSpr -> the attribute contains Uri
 * instead of Bitmap.
 *
 * Useful for creating Backup
 */
data class ImageSprUri(
    val kavlingKode: String,
    val uriStr: String,
)
