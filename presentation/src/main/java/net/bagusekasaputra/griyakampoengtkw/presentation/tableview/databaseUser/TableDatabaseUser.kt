package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.databaseUser

import net.bagusekasaputra.griyakampoengtkw.domain.entity.DatabaseUser
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.ITableView
import net.bagusekasaputra.griyakampoengtkw.presentation.toSlashedDate

class TableDatabaseUser(
    private val listDatabaseUser: List<DatabaseUser>,
): ITableView<TableDatabaseUser.CalonPembeliColumnHeader, TableDatabaseUser.CalonPembeliRowHeader, TableDatabaseUser.CalonPembeliCell> {
    companion object {
        const val COLUMN_NAMA = 0
        const val COLUMN_TANGGAL = 1
        const val COLUMN_NO_HP = 2
        const val COLUMN_TIKTOK = 3
        const val COLUMN_LOKASI_INDO = 4
        const val COLUMN_NEGARA_BEKERJA = 5
        const val COLUMN_KETERANGAN = 6
    }

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
            CalonPembeliColumnHeader("Tanggal"),
            CalonPembeliColumnHeader("No Hp"),
            CalonPembeliColumnHeader("Tiktok"),
            CalonPembeliColumnHeader("Lok. Indo"),
            CalonPembeliColumnHeader("Negara Bekerja"),
            CalonPembeliColumnHeader("Keterangan"),
        )
    }

    override fun getRowHeaderItems(): List<CalonPembeliRowHeader> {
        val listRowHeader = mutableListOf<CalonPembeliRowHeader>()

        repeat(listDatabaseUser.size) {
            listRowHeader.add(
                CalonPembeliRowHeader(it)
            )
        }

        return listRowHeader
    }

    override fun getCellItems(): List<List<CalonPembeliCell>> {
        val listCell = mutableListOf<List<CalonPembeliCell>>()

        listDatabaseUser.forEach { calonPembeli ->
            val cell = mutableListOf<CalonPembeliCell>()

            cell.add(CalonPembeliCell(calonPembeli.nama))
            cell.add(CalonPembeliCell(calonPembeli.tanggal.toSlashedDate()))
            cell.add(CalonPembeliCell(calonPembeli.noHp))
            cell.add(CalonPembeliCell(calonPembeli.usernameTiktok))
            cell.add(CalonPembeliCell(calonPembeli.lokasiIndo))
            cell.add(CalonPembeliCell(calonPembeli.negaraBekerja))
            cell.add(CalonPembeliCell(calonPembeli.keterangan))

            listCell.add(cell)
        }

        return listCell
    }

}