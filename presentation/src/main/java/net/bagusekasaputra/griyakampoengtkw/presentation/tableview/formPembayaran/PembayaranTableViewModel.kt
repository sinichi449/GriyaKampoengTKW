package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran

data class PembayaranCell(
    val mData: Any?,
)

data class PembayaranColumnHeader(
    val text: String?,
)

data class PembayaranRowHeader(
    val text: String?,
    val sudahIsiFoto: Boolean = false,
)