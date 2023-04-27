package net.bagusekasaputra.griyakampoengtkw.data.model

data class PengingatModel(
    var id: Long? = null,
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val time: String = "",
    var isActive: Boolean = false,
) {
}