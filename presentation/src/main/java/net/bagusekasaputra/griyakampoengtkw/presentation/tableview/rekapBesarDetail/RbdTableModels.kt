package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail

/**
 * RBD = Rekap Besar Detail
 */
data class RbdColumnHeader(
    val text: String,
)

data class RbdWithKavlingRowHeader(
    val nomor: String,
    val kavling: String,
)

data class RbdCell(
    val text: String,
)