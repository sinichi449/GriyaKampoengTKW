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
    private val columnHeaderWidths = listOf(
        Pair(BULAN, 250),
        Pair(UANG_MASUK, 300),
        Pair(TUNGGAKAN, 300),
        Pair(ALOKASI, 300),
        Pair(KELUNASAN, 250),
    )

    init {
        val background = R.color.purple_500

        setAdditionalColumnHeaderActions { columnHeaderViewHolder, _, _ ->
            columnHeaderViewHolder.containerBackground = background
        }
        setAdditionalRowHeaderActions { rowHeaderViewHolder, rowHeaderItem, _ ->
            val viewHolder = rowHeaderViewHolder as GktTableViewAdapter.MySingleRowHeaderViewHolder

            viewHolder.tvRowHeader.typeface = Typeface.SANS_SERIF

            val nomorAndKelunasan = getNomorAndKelunasan(rowHeaderItem)
            viewHolder.tvRowHeader.text = nomorAndKelunasan.first
            viewHolder.containerBackground = when (nomorAndKelunasan.second) {
                Kelunasan.LUNAS.name -> R.color.pembayaran_bulanan_lunas
                Kelunasan.KURANG.name -> R.color.pembayaran_bulanan_belum_lunas
                else -> R.color.pembayaran_bulanan_nil
            }
        }
        setAdditionalCornerViewActions { view, _ ->
            view.setBackgroundColor(ContextCompat.getColor(
                tablePembayaranBulanan.context, background,
            ))
        }
        setAdditionalCellActions { cellViewHolder, cellItem, column, _ ->
            when (column) {
                UANG_MASUK, TUNGGAKAN -> cellViewHolder.tvCell.typeface = Typeface.SERIF
                ALOKASI -> {
                    cellViewHolder.tvCell.typeface = Typeface.SERIF

                    val alokasiStr = cellItem?.getText() ?: "0"
                    val alokasi = NumberUtil.formatStringToLong(alokasiStr)
                    cellViewHolder.cellBackgroundColor = if (alokasi <= 0)
                            android.R.color.darker_gray else R.color.white
                }
                KELUNASAN -> {
                    cellViewHolder.tvCell.typeface = Typeface.DEFAULT_BOLD

                    cellViewHolder.cellBackgroundColor = when (cellItem?.getText()) {
                        Kelunasan.LUNAS.str -> R.color.pembayaran_bulanan_lunas
                        Kelunasan.KURANG.str -> R.color.pembayaran_bulanan_belum_lunas
                        else -> R.color.pembayaran_bulanan_nil
                    }
                }
            }
        }

        setWidthColumnHeader(columnHeaderWidths)
    }

    companion object {
        const val BULAN = 0
        const val UANG_MASUK = 1
        const val TUNGGAKAN = 2
        const val ALOKASI = 3
        const val KELUNASAN = 4
    }

    override fun getColumnHeaderItems(): List<ColumnHeader> {
        val columnHeaders = mutableListOf<PbColumnHeader>()
        columnHeaders.apply {
            add(BULAN, PbColumnHeader("Bulan"))
            add(UANG_MASUK, PbColumnHeader("Uang Masuk"))
            add(TUNGGAKAN, PbColumnHeader("Tunggakan"))
            add(ALOKASI, PbColumnHeader("Alokasi"))
            add(KELUNASAN, PbColumnHeader("Kelunasan"))
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
                add(UANG_MASUK, PbCell(NumberUtil.formatLongToString(it.uangMasuk)))
                add(TUNGGAKAN, PbCell(NumberUtil.formatLongToString(it.tunggakan)))
                add(ALOKASI, PbCell(NumberUtil.formatLongToString(it.alokasi)))
                add(KELUNASAN, PbCell(it.kelunasan.str))
            }

            cellItems.add(item)
        }

        return cellItems
    }

    private fun getNomorAndKelunasan(rowHeaderItem: RowHeader?): Pair<String, String> {
        val nomorAndKelunasan = rowHeaderItem?.getText()?.split(separatorKelunasan)
        val nomor = nomorAndKelunasan?.get(0).toString()
        val kelunasan = nomorAndKelunasan?.get(1).toString()

        return Pair(nomor, kelunasan)
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