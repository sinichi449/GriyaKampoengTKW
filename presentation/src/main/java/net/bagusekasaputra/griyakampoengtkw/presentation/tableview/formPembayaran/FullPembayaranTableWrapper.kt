package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran

import android.graphics.Typeface
import android.view.Gravity
import com.evrencoskun.tableview.TableView
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.AbstractTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.GktTableViewAdapter

class FullPembayaranTableWrapper(
    private val tableFullPembayaran: TableView,
    private val pembayarans: List<Pembayaran>,
): AbstractTableWrapper(tableFullPembayaran) {

    private val cornerSeparator = "<>"
    private val cornerTitle = "Termin"
    var widthColumnHeaders = listOf(
        Pair(TANGGAL, 250),
        Pair(UANG_DIBAYAR, 350),
        Pair(TOTAL, 350),
        Pair(PERSENTASE, 250),
        Pair(KETERANGAN_PROGRESS, 500),
    )

    init {
        useDoubleCorner(cornerTitle, cornerSeparator)

        setWidthColumnHeader(widthColumnHeaders)

        setAdditionalCellActions { cellViewHolder: GktTableViewAdapter.MyCellViewHolder, _: CellItem?, column: Int, _: Int ->
            when (column) {
                UANG_DIBAYAR, TOTAL -> cellViewHolder.tvCell.typeface = Typeface.SERIF
                PERSENTASE -> cellViewHolder.tvCell.typeface = Typeface.MONOSPACE
                KETERANGAN_PROGRESS -> cellViewHolder.tvCell.gravity = Gravity.START
            }
        }

        setAdditionalRowHeaderActions { rowHeaderViewHolder, rowHeaderItem, _ ->
            val parseRowHeader = rowHeaderItem?.getText()?.split(cornerSeparator)
            val sudahIsiFotoPembayaran = parseRowHeader?.get(2)?.toBoolean()

            val viewHolder = rowHeaderViewHolder as GktTableViewAdapter.MyDoubleRowHeaderViewHolder
            val backgroundColor = if (sudahIsiFotoPembayaran == true) 
                R.color.table_selected_color else R.color.white

            viewHolder.containerBackground = backgroundColor
            viewHolder.rowHeadersTextColor = R.color.black
        }
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
            val sudahIsiFoto = pembayaran.sudahIsiFotoPembayaran
            val cornerAndRhData = "${nomor}${cornerSeparator}${pembayaran.termin}" +
                    "${cornerSeparator}$sudahIsiFoto"

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