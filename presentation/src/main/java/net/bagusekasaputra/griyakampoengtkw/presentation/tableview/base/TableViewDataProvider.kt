package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base

interface TableViewDataProvider<T> {

    fun getColumnHeaders(data: Collection<T>): List<ColumnHeader>

    fun getRowHeaders(data: Collection<T>): List<RowHeader>

    fun getCellItems(data: Collection<T>): List<List<CellItem>>

}