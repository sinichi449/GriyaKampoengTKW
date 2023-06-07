package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal

import com.evrencoskun.tableview.sort.ISortableModel

data class RgColumnHeader(
    val text: String,
)

data class RgRowHeader(
    val nomor: String,
    val kavling: String,
): ISortableModel {
    override fun getId(): String {
        return nomor
    }

    override fun getContent(): Any {
        return kavling
    }

}

data class RgCell(
    val kavling: String,
    val data: Any?,
): ISortableModel {
    override fun getId(): String {
        return kavling
    }

    override fun getContent(): Any? {
        return data
    }

}