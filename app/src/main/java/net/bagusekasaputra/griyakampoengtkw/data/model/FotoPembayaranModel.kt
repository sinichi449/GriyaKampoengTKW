package net.bagusekasaputra.griyakampoengtkw.data.model

import android.net.Uri

data class FotoPembayaranModel(
    val id: Long? = null,
    val kavlingKode: String = "",
    val termin: String = "",
    val uriStr: String = "",
) {

    fun getUri() = Uri.parse(uriStr)
}