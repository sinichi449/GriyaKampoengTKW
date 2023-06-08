package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran

import android.graphics.Typeface
import android.view.Gravity
import com.evrencoskun.tableview.TableView
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.legacy.LegacyTableWrapper
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.legacy.LegacyTableViewAdapter

/**
 * Ordering for index column matters!!
 */
class FullPembayaranTableWrapper(
    tableFullPembayaran: TableView,
    private val pembayarans: List<Pembayaran>,
): LegacyTableWrapper(tableFullPembayaran) {

    private val cornerSeparator = "<>"
    private val cornerTitle = "Termin"
    private val widthColumnHeaders = listOf(
        Pair(INVOICE, 250),
        Pair(TANGGAL, 250),
        Pair(UANG_DIBAYAR, 350),
        Pair(TOTAL, 350),
        Pair(PERSENTASE, 300),
        Pair(KETERANGAN_PROGRESS, 500),
    )

    init {
        useDoubleCorner(cornerTitle, cornerSeparator)

        setWidthColumnHeader(widthColumnHeaders)

        setAdditionalCellActions { cellViewHolder, _, column, _ ->
            when (column) {
                UANG_DIBAYAR, TOTAL -> cellViewHolder.tvCell.typeface = Typeface.SERIF
                PERSENTASE -> cellViewHolder.tvCell.typeface = Typeface.MONOSPACE
                KETERANGAN_PROGRESS -> cellViewHolder.tvCell.gravity = Gravity.START
            }
        }

        setAdditionalRowHeaderActions { rowHeaderViewHolder, rowHeaderItem, _ ->
            val parseRowHeader = rowHeaderItem?.getText()?.split(cornerSeparator)
            val termin = parseRowHeader?.get(1) ?: "NULL"
            val sudahIsiFotoPembayaran = parseRowHeader?.get(2)?.toBoolean()
            val sudahAmbilKuitansi = parseRowHeader?.get(3)?.toBoolean()

            val viewHolder = rowHeaderViewHolder as LegacyTableViewAdapter.MyDoubleRowHeaderViewHolder

            // Set Background color if sudah isi foto
            val backgroundColor = if (sudahIsiFotoPembayaran == true) 
                R.color.table_selected_color else R.color.white
            viewHolder.containerBackground = backgroundColor
            viewHolder.rowHeadersTextColor = R.color.black

            // Set termin indikator (*) if sudah ambil kuitansi
            val terminText: String = if (sudahAmbilKuitansi == true) {
                "$termin (*)"
            } else {
                termin
            }
            viewHolder.tvData.text = terminText

            // Set gravity to start
            viewHolder.tvData.gravity = Gravity.START
        }
    }

    data class PbColumnHeader(val columnHeaderText: String): LegacyColumnHeader {
        override fun getText(): String {
            return columnHeaderText
        }
    }

    data class PbCellItem(val cellText: String): LegacyCellItem {
        override fun getText(): String {
            return cellText
        }
    }

    // Row Header contains the following: No, Termin, Sudah Isi Foto, Sudah Ambil Kuitansi
    data class PbRowHeader(val cornerAndRhData: String): LegacyRowHeader {
        override fun getText(): String {
            return cornerAndRhData
        }
    }

    override suspend fun getColumnHeaderItems(): List<LegacyColumnHeader> {
        val columnHeaders = mutableListOf<PbColumnHeader>()
        columnHeaders.apply {
            add(INVOICE, PbColumnHeader("Invoice"))
            add(TANGGAL, PbColumnHeader("Tanggal"))
            add(UANG_DIBAYAR, PbColumnHeader("Uang Dibayar"))
            add(TOTAL, PbColumnHeader("Total"))
            add(PERSENTASE, PbColumnHeader("Persentase"))
            add(KETERANGAN_PROGRESS, PbColumnHeader("Keterangan Progress"))
        }

        return columnHeaders
    }

    override suspend fun getRowHeaderItems(): List<LegacyRowHeader> {
        val rowHeaders = mutableListOf<PbRowHeader>()
        pembayarans.forEachIndexed { index, pembayaran ->
            val nomor = index.plus(1).toString()
            val sudahIsiFoto = pembayaran.sudahIsiFotoPembayaran
            val sudahAmbilKuitansi = pembayaran.sudahAmbilKuitansi

            val cornerAndRhData = StringBuilder().apply {
                append("$nomor${cornerSeparator}")
                append("${pembayaran.termin}${cornerSeparator}")
                append("$sudahIsiFoto${cornerSeparator}")
                append("$sudahAmbilKuitansi")
            }.toString()
            rowHeaders.add(PbRowHeader(cornerAndRhData))
        }

        return rowHeaders
    }

    override suspend fun getCellItems(): List<List<LegacyCellItem>> {
        val cellItems = mutableListOf<List<LegacyCellItem>>()
        pembayarans.forEach {
            val items = mutableListOf<LegacyCellItem>()
            items.apply {
                add(INVOICE, PbCellItem(it.bulanAngsuran.bulanAndTahun))
                add(TANGGAL, PbCellItem(it.tanggal))
                add(UANG_DIBAYAR, PbCellItem(it.jumlahUangDibayar))
                add(TOTAL, PbCellItem(it.totalUangMasuk))
                add(PERSENTASE, PbCellItem("${it.presentase}%"))
                add(KETERANGAN_PROGRESS, PbCellItem(it.keterangan))
            }

            cellItems.add(items)
        }

        return cellItems
    }


    companion object {
        const val INVOICE = 0
        const val TANGGAL = 1
        const val UANG_DIBAYAR = 2
        const val TOTAL = 3
        const val PERSENTASE = 4
        const val KETERANGAN_PROGRESS = 5
    }
}