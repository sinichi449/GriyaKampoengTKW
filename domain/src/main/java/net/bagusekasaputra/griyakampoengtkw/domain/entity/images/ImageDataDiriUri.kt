package net.bagusekasaputra.griyakampoengtkw.domain.entity.images


/**
 * Difference with ImageDataDiri -> the attribute contains Uri
 * instead of Bitmap.
 *
 * Useful for creating Backup
 */
data class ImageDataDiriUri(
    val kavling: String,
    val uriStr: String,
)