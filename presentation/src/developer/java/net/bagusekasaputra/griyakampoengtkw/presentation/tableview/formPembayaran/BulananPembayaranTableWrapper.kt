package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran

import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.TableView
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.AbstractTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.GktTableViewAdapter

class BulananPembayaranTableWrapper(
    private val tablePembayaranBulanan: TableView,
    private val pembayaranBulanans: List<PembayaranBulanan>,
): AbstractTableWrapper(tablePembayaranBulanan) {

    init {
        val background = R.color.purple_500

        setAdditionalColumnHeaderActions { columnHeaderViewHolder, _, _ ->
            columnHeaderViewHolder.containerBackground = background
        }
        setAdditionalRowHeaderActions { rowHeaderViewHolder, rowHeaderItem, row ->
            val viewHolder = rowHeaderViewHolder as GktTableViewAdapter.MySingleRowHeaderViewHolder
            viewHolder.tvRowHeader.typeface = Typeface.SANS_SERIF
        }
        setAdditionalCornerViewActions { view, _ ->
            view.setBackgroundColor(ContextCompat.getColor(
                tablePembayaranBulanan.context, background,
            ))
        }
        setAdditionalCellActions { cellViewHolder, _, column, _ ->
            when (column) {
                UANG_MASUK, JUMLAH_TUNGGAKAN -> cellViewHolder.tvCell.typeface = Typeface.SERIF
            }
        }
    }

    private data class PbColumnHeader(val columnHeaderText: String): ColumnHeader {
        override fun getText(): String {
            return columnHeaderText
        }
    }

    private data class PbRowHeader(val nomor: Int): RowHeader {
        override fun getText(): String {
            return nomor.toString()
        }
    }

    private class PbCell(val cellText: String): CellItem {
        override fun getText(): String {
            return cellText
        }
    }

    companion object {
        const val BULAN = 0
        const val UANG_MASUK = 1
        const val JUMLAH_TUNGGAKAN = 2
    }

    override fun getColumnHeaderItems(): List<ColumnHeader> {
        val columnHeaders = mutableListOf<PbColumnHeader>()
        columnHeaders.apply {
            add(BULAN, PbColumnHeader("Bulan"))
            add(UANG_MASUK, PbColumnHeader("Uang Masuk"))
            add(JUMLAH_TUNGGAKAN, PbColumnHeader("Jumlah Tunggakan"))
        }

        return columnHeaders
    }

    override fun getRowHeaderItems(): List<RowHeader> {
        val pbRowHeaders = mutableListOf<PbRowHeader>()
        repeat(pembayaranBulanans.size) {
            pbRowHeaders.add(PbRowHeader(it.plus(1)))
        }

        return pbRowHeaders
    }

    override fun getCellItems(): List<List<CellItem>> {
        val cellItems = mutableListOf<List<CellItem>>()
        pembayaranBulanans.forEach {
            val item = mutableListOf<CellItem>().apply {
                add(BULAN, PbCell(it.parsedBulanTahun))
                add(UANG_MASUK, PbCell(NumberUtil.formatLongToString(it.totalUangMasuk)))
                add(JUMLAH_TUNGGAKAN, PbCell(NumberUtil.formatLongToString(it.totalTunggakan)))
            }

            cellItems.add(item)
        }

        return cellItems
    }
}