package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran

import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.TableView
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan.Kelunasan
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.AbstractTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.GktTableViewAdapter

class BulananPembayaranTableWrapper(
    private val tablePembayaranBulanan: TableView,
    private val pembayaranBulanans: List<PembayaranBulanan>,
): AbstractTableWrapper(tablePembayaranBulanan) {

    private val separatorKelunasan = "<>"

    init {
        val background = R.color.purple_500

        setAdditionalColumnHeaderActions { columnHeaderViewHolder, _, _ ->
            columnHeaderViewHolder.containerBackground = background
        }
        setAdditionalRowHeaderActions { rowHeaderViewHolder, rowHeaderItem, _ ->
            val viewHolder = rowHeaderViewHolder as GktTableViewAdapter.MySingleRowHeaderViewHolder

            viewHolder.tvRowHeader.typeface = Typeface.SANS_SERIF

            val nomorAndKelunasan = rowHeaderItem?.getText()?.split(separatorKelunasan)
            val nomor = nomorAndKelunasan?.get(0)
            val kelunasan = nomorAndKelunasan?.get(1)

            viewHolder.tvRowHeader.text = nomor ?: "0"
            viewHolder.containerBackground = when (kelunasan) {
                Kelunasan.LUNAS.name -> R.color.pembayaran_bulanan_lunas
                Kelunasan.BELUM_LUNAS.name -> R.color.pembayaran_bulanan_belum_lunas
                else -> R.color.pembayaran_bulanan_nil
            }
        }
        setAdditionalCornerViewActions { view, _ ->
            view.setBackgroundColor(ContextCompat.getColor(
                tablePembayaranBulanan.context, background,
            ))
        }
        setAdditionalCellActions { cellViewHolder, _, column, _ ->
            when (column) {
                UANG_MASUK, JUMLAH_TUNGGAKAN, ALOKASI -> cellViewHolder.tvCell.typeface = Typeface.SERIF
                STATUS -> cellViewHolder.tvCell.typeface = Typeface.DEFAULT_BOLD
            }
        }
    }

    companion object {
        const val BULAN = 0
        const val UANG_MASUK = 1
        const val JUMLAH_TUNGGAKAN = 2
        const val ALOKASI = 3
        const val STATUS = 4
    }

    override fun getColumnHeaderItems(): List<ColumnHeader> {
        val columnHeaders = mutableListOf<PbColumnHeader>()
        columnHeaders.apply {
            add(BULAN, PbColumnHeader("Bulan"))
            add(UANG_MASUK, PbColumnHeader("Uang Masuk"))
            add(JUMLAH_TUNGGAKAN, PbColumnHeader("Jumlah Tunggakan"))
            add(ALOKASI, PbColumnHeader("Alokasi"))
            add(STATUS, PbColumnHeader("Status"))
        }

        return columnHeaders
    }

    override fun getRowHeaderItems(): List<RowHeader> {
        val pbRowHeaders = mutableListOf<PbRowHeader>()
        pembayaranBulanans.forEachIndexed { index, pembayaranBulanan ->
            val nomorAndKelunasan = "${index.plus(1)}${separatorKelunasan}${pembayaranBulanan.kelunasan.name}"

            pbRowHeaders.add(PbRowHeader(nomorAndKelunasan))
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
                add(ALOKASI, PbCell(NumberUtil.formatLongToString(it.alokasi)))
                add(STATUS, PbCell(it.kelunasan.str))
            }

            cellItems.add(item)
        }

        return cellItems
    }

    private data class PbColumnHeader(val columnHeaderText: String): ColumnHeader {
        override fun getText(): String {
            return columnHeaderText
        }
    }

    private data class PbRowHeader(val nomorAndKelunasan: String): RowHeader {
        override fun getText(): String {
            return nomorAndKelunasan
        }
    }

    private class PbCell(val cellText: String): CellItem {
        override fun getText(): String {
            return cellText
        }
    }
}