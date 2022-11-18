package net.bagusekasaputra.griyakampoengtkw.ui

// For transporting need-to-be-full-screened-image from any activity to FullImageActivity
data class ImageTransport<T>(
    val sendIntention: String,
    val content: T,
): java.io.Serializable