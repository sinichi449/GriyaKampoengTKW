package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaLain

data class BlColumnHeader(
    val text: String,
) {
    companion object {
        fun getColumnHeaders(): List<BlColumnHeader> {
            return listOf(
                BlColumnHeader("Jenis Biaya"),
                BlColumnHeader("Harga"),
                BlColumnHeader("Tanggal"),
            )
        }
    }
}

data class BlRowHeader(
    val nomor: String,
) {
    companion object {
        fun getRowHeaders(listSize: Int): List<BlRowHeader> {
            return if (listSize > 0) {
                val rowHeaders = mutableListOf<BlRowHeader>()
                (1..listSize).forEach {
                    rowHeaders.add(BlRowHeader(it.toString()))
                }

                rowHeaders
            } else {
                listOf(
                    BlRowHeader("0")
                )
            }
        }
    }
}

data class BlCell(
    val text: String,
) {
    // TODO
}