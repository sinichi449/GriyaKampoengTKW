package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base

import com.evrencoskun.tableview.sort.ISortableModel

data class ColumnHeader(
    val text: String,
)

data class RowHeader(
    val rowId: String,
    val data: String
): ISortableModel {
    override fun getId(): String {
        return rowId
    }

    override fun getContent(): Any {
        return data
    }
}

data class CellItem(
    val cellId: String,
    val data: Any?,
): ISortableModel {
    override fun getId(): String {
        return cellId
    }

    override fun getContent(): Any? {
        return data
    }
}