package net.bagusekasaputra.griyakampoengtkw.presentation

import net.bagusekasaputra.griyakampoengtkw.domain.DataMode

// For transporting need-to-be-full-screened-image from any activity to FullImageActivity
data class ImageTransport<T>(
    val sendIntention: String,
    val content: T,
    val dataMode: DataMode,
): java.io.Serializable