package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.formPembayaran

import com.evrencoskun.tableview.TableView
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.AbstractTableWrapper

class BulananPembayaranTableWrapper(
    tablePembayaranBulanan: TableView,
    private val pembayaranBulanans: List<PembayaranBulanan>,
): AbstractTableWrapper(tablePembayaranBulanan) {

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
        return listOf(
            PbColumnHeader("Bulan"),
            PbColumnHeader("Uang Masuk"),
            PbColumnHeader("Jumlah Tunggakan"),
        )
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
                add(PbCell(it.parsedBulanTahun))
                add(PbCell(NumberUtil.formatLongToString(it.totalUangMasuk)))
                add(PbCell(NumberUtil.formatLongToString(it.totalTunggakan)))
            }

            cellItems.add(item)
        }

        return cellItems
    }
}