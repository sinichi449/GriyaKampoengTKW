package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base

import com.evrencoskun.tableview.filter.IFilterableModel
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
): ISortableModel, IFilterableModel {
    override fun getId(): String {
        return cellId
    }

    override fun getContent(): Any? {
        return data
    }

    override fun getFilterableKeyword(): String {
        return data?.toString() ?: ""
    }
}