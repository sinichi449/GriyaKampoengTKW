package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.calonPembeli

import net.bagusekasaputra.griyakampoengtkw.domain.entity.CalonPembeli
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.ITableView

class TableCalonPembeli(
    private val listCalonPembeli: List<CalonPembeli>,
): ITableView<TableCalonPembeli.CalonPembeliColumnHeader, TableCalonPembeli.CalonPembeliRowHeader, TableCalonPembeli.CalonPembeliCell> {

    data class CalonPembeliColumnHeader(
        val text: String,
    )

    data class CalonPembeliRowHeader(
        val nomor: Int,
    )

    data class CalonPembeliCell(
        val text: String,
    )

    override fun getColumnHeaderItems(): List<CalonPembeliColumnHeader> {
        return listOf(
            CalonPembeliColumnHeader("Nama"),
            CalonPembeliColumnHeader("No Hp"),
            CalonPembeliColumnHeader("Tiktok"),
            CalonPembeliColumnHeader("Keterangan"),
        )
    }

    override fun getRowHeaderItems(): List<CalonPembeliRowHeader> {
        val listRowHeader = mutableListOf<CalonPembeliRowHeader>()

        repeat(listCalonPembeli.size) {
            listRowHeader.add(
                CalonPembeliRowHeader(it)
            )
        }

        return listRowHeader
    }

    override fun getCellItems(): List<List<CalonPembeliCell>> {
        val listCell = mutableListOf<List<CalonPembeliCell>>()

        listCalonPembeli.forEach { calonPembeli ->
            val cell = mutableListOf<CalonPembeliCell>()

            cell.add(CalonPembeliCell(calonPembeli.nama))
            cell.add(CalonPembeliCell(calonPembeli.noHp))
            cell.add(CalonPembeliCell(calonPembeli.usernameTiktok))
            cell.add(CalonPembeliCell(calonPembeli.keterangan))

            listCell.add(cell)
        }

        return listCell
    }

}