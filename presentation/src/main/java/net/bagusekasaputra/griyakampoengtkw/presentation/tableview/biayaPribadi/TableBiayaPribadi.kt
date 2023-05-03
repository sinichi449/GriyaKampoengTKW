package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaPribadi

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaPribadi
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.ITableView
import net.bagusekasaputra.griyakampoengtkw.presentation.toSlashedDate

class TableBiayaPribadi(
    private val listBiayaPribadi: List<BiayaPribadi>
): ITableView<TableBiayaPribadi.BpColumnHeader, TableBiayaPribadi.BpRowHeader, TableBiayaPribadi.BpCell> {

    companion object {
        const val COLUMN_JENIS_BIAYA = 0
        const val COLUMN_TANGGAL = 1
        const val COLUMN_HARGA = 2
    }

    data class BpColumnHeader(val text: String)
    data class BpRowHeader(val nomor: Int)
    data class BpCell(val text: String)


    override fun getColumnHeaderItems(): List<BpColumnHeader> {
        return listOf(
            BpColumnHeader("Jenis Biaya"),
            BpColumnHeader("Tanggal"),
            BpColumnHeader("Harga"),
        )
    }

    override fun getRowHeaderItems(): List<BpRowHeader> {
        val rowHeaders = mutableListOf<BpRowHeader>()

        repeat(listBiayaPribadi.size) {
            rowHeaders.add(BpRowHeader(it))
        }

        return rowHeaders
    }

    override fun getCellItems(): List<List<BpCell>> {
        val cellItems = mutableListOf<List<BpCell>>()

        listBiayaPribadi.forEach { biayaPribadi ->
            val cell = mutableListOf<BpCell>()

            cell.add(BpCell(biayaPribadi.jenisBiaya))
            cell.add(BpCell(biayaPribadi.tanggal.toSlashedDate()))
            cell.add(BpCell(NumberUtil.formatLongToString(biayaPribadi.harga)))

            cellItems.add(cell)
        }

        return cellItems
    }
}