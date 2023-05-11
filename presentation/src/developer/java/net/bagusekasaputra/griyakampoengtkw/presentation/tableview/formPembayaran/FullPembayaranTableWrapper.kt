package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran

import com.evrencoskun.tableview.TableView
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.AbstractTableWrapper

class FullPembayaranTableWrapper(
    tableFullPembayaran: TableView,
    private val pembayarans: List<Pembayaran>,
): AbstractTableWrapper(tableFullPembayaran) {

    private val cornerSeparator = "<>"
    private val cornerTitle = "Termin"

    init {
        useDoubleCorner(cornerTitle, cornerSeparator)
    }

    data class PbColumnHeader(val columnHeaderText: String): ColumnHeader {
        override fun getText(): String {
            return columnHeaderText
        }
    }

    data class PbCellItem(val cellText: String): CellItem {
        override fun getText(): String {
            return cellText
        }
    }

    data class PbRowHeader(val cornerAndRhData: String): RowHeader {
        override fun getText(): String {
            return cornerAndRhData
        }
    }

    override fun getColumnHeaderItems(): List<ColumnHeader> {
        val columnHeaders = mutableListOf<PbColumnHeader>()
        columnHeaders.apply {
            add(TANGGAL, PbColumnHeader("Tanggal"))
            add(UANG_DIBAYAR, PbColumnHeader("Uang Dibayar"))
            add(TOTAL, PbColumnHeader("Total"))
            add(PERSENTASE, PbColumnHeader("(%)"))
            add(KETERANGAN_PROGRESS, PbColumnHeader("Keterangan Progress"))
        }

        return columnHeaders
    }

    override fun getRowHeaderItems(): List<RowHeader> {
        val rowHeaders = mutableListOf<PbRowHeader>()
        pembayarans.forEachIndexed { index, pembayaran ->
            val nomor = index.plus(1).toString()
            val cornerAndRhData = "${nomor}${cornerSeparator}${pembayaran.termin}"

            rowHeaders.add(PbRowHeader(cornerAndRhData))
        }

        return rowHeaders
    }

    override fun getCellItems(): List<List<CellItem>> {
        val cellItems = mutableListOf<List<CellItem>>()
        pembayarans.forEach {
            val items = mutableListOf<CellItem>()
            items.apply {
                add(TANGGAL, PbCellItem(it.tanggal))
                add(UANG_DIBAYAR, PbCellItem(it.jumlahUangDibayar))
                add(TOTAL, PbCellItem(it.totalUangMasuk))
                add(PERSENTASE, PbCellItem(it.presentase.toString()))
                add(KETERANGAN_PROGRESS, PbCellItem(it.keterangan))
            }

            cellItems.add(items)
        }

        return cellItems
    }


    companion object {
        const val TANGGAL = 0
        const val UANG_DIBAYAR = 1
        const val TOTAL = 2
        const val PERSENTASE = 3
        const val KETERANGAN_PROGRESS = 4
    }
}