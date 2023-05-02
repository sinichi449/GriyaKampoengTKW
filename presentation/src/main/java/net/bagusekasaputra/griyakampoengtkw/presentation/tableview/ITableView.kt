package net.bagusekasaputra.griyakampoengtkw.presentation.tableview

interface ITableView<CH: Any, RH: Any, CL: Any> {

    fun getColumnHeaderItems(): List<CH>

    fun getRowHeaderItems(): List<RH>

    fun getCellItems(): List<List<CL>>

}