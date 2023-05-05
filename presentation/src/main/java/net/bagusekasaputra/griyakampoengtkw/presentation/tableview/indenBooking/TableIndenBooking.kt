package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.indenBooking

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.ITableView
import net.bagusekasaputra.griyakampoengtkw.presentation.toSlashedDate

class TableIndenBooking(
    private val listIndenBooking: List<IndenBooking>,
): ITableView<TableIndenBooking.IbColumnHeader, TableIndenBooking.IbRowHeader, TableIndenBooking.IbCell> {

    companion object {
        const val COLUMN_NAMA_COSTUMER = 0
        const val COLUMN_TANGGAL_DIBAYAR = 1
        const val COLUMN_JUMLAH_UANG = 2
        const val COLUMN_NO_HP = 3
        const val COLUMN_KETERANGAN = 4

    }

    class IbColumnHeader(val text: String)

    class IbRowHeader(val nomor: Int, val sudahIsiFoto: Boolean)

    class IbCell(val text: String)

    override fun getColumnHeaderItems(): List<IbColumnHeader> {
        return listOf(
            IbColumnHeader("Nama Costumer"),
            IbColumnHeader("Tanggal Dibayar"),
            IbColumnHeader("Jumlah Uang"),
            IbColumnHeader("No Hp"),
            IbColumnHeader("Keterangan"),
        )
    }

    override fun getRowHeaderItems(): List<IbRowHeader> {
        val listRowHeader = mutableListOf<IbRowHeader>()
        listIndenBooking.forEachIndexed { index, indenBooking ->
            val sudahIsiFoto = indenBooking.fotoPembayaranPath.isNotEmpty()

            listRowHeader.add(IbRowHeader(index.plus(1), sudahIsiFoto))
        }

        return listRowHeader
    }

    override fun getCellItems(): List<List<IbCell>> {
        val listCellItem = mutableListOf<List<IbCell>>()
        listIndenBooking.forEach { indenBooking ->
            val cell = mutableListOf<IbCell>()

            cell.add(IbCell(indenBooking.namaCostumer))
            cell.add(IbCell(indenBooking.tanggalDibayar.toSlashedDate()))
            cell.add(IbCell(NumberUtil.formatLongToString(indenBooking.jumlahUang)))
            cell.add(IbCell(indenBooking.noHp))
            cell.add(IbCell(indenBooking.keterangan))

            listCellItem.add(cell)
        }

        return listCellItem
    }


}