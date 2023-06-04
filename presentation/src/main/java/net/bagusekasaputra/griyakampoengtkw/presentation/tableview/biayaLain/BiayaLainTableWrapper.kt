package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaLain

import android.graphics.Typeface
import android.view.Gravity
import com.evrencoskun.tableview.TableView
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.AbstractTableWrapper

class BiayaLainTableWrapper(
    tableViewBiayaLain: TableView,
    private val biayaLains: List<BiayaLain>,
): AbstractTableWrapper(tableViewBiayaLain) {

    private val columHeaderWidths = listOf(
        Pair(JENIS_BIAYA, 500),
        Pair(HARGA, 300),
        Pair(TANGGAL, 300),
    )

    init {
        setWidthColumnHeader(columHeaderWidths)

        setAdditionalCellActions { cellViewHolder, _, column, _ ->
            when (column) {
                JENIS_BIAYA -> cellViewHolder.tvCell.gravity = Gravity.START
                HARGA -> cellViewHolder.tvCell.typeface = Typeface.SERIF
            }
        }

        setAdditionalColumnHeaderActions { columnHeaderViewHolder, _, _ ->
            columnHeaderViewHolder.containerBackground = R.color.abang
            columnHeaderViewHolder.textColumnHeaderColor = R.color.white
        }
    }

    override fun getColumnHeaderItems(): List<ColumnHeader> {
        val columnHeaders = mutableListOf<BlColumnHeader>()
        columnHeaders.apply {
            add(JENIS_BIAYA, BlColumnHeader("Jenis Biaya"))
            add(HARGA, BlColumnHeader("Harga"))
            add(TANGGAL, BlColumnHeader("Tanggal"))
        }

        return columnHeaders
    }

    override fun getRowHeaderItems(): List<RowHeader> {
        val rowHeaders = mutableListOf<BlRowHeader>()
        repeat(biayaLains.size) {
            rowHeaders.add(BlRowHeader(it.plus(1)))
        }

        return rowHeaders
    }

    override fun getCellItems(): List<List<CellItem>> {
        val cellItems = mutableListOf<List<BlCell>>()
        biayaLains.forEach {
            val cell = mutableListOf<BlCell>()
            cell.apply {
                add(JENIS_BIAYA, BlCell(it.jenisBiaya))
                add(HARGA, BlCell(NumberUtil.formatLongToString(it.harga)))
                add(TANGGAL, BlCell(it.tanggal))
            }

            cellItems.add(cell)
        }

        return cellItems
    }


    companion object {
        const val JENIS_BIAYA = 0
        const val HARGA = 1
        const val TANGGAL = 2
    }

    data class BlColumnHeader(val mData: String): ColumnHeader {
        override fun getText(): String {
            return mData
        }

    }

    data class BlRowHeader(val nomor: Int): RowHeader {
        override fun getText(): String {
            return nomor.toString()
        }
    }

    data class BlCell(val mData: String): CellItem {
        override fun getText(): String {
            return mData
        }
    }
}