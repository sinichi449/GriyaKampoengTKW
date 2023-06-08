package net.bagusekasaputra.griyakampoengtkw.presentation.tableview

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.Gravity
import com.evrencoskun.tableview.TableView
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DatabaseUser
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.legacy.LegacyTableWrapper

class DatabaseUserTableWrapper(
    tableDatabaseUser: TableView,
    private val users: List<DatabaseUser>,
): LegacyTableWrapper(tableDatabaseUser) {

    private val columnHeaderWidths = listOf(
        Pair(NAMA, 350),
        Pair(TANGGAL, 300),
        Pair(NO_HP, 400),
        Pair(TIKTOK, 400),
        Pair(LOKASI_INDO, 500),
        Pair(NEGARA_BEKERJA, 300),
        Pair(KETERANGAN, 500),
    )

    init {
        setWidthColumnHeader(columnHeaderWidths)

        setAdditionalCellActions { cellViewHolder, cellItem, column, row ->
            when (column) {
                NO_HP -> {
                    val nomorHp = cellItem?.getText()
                    if (!nomorHp.isNullOrEmpty()) {
                        val underlineText = SpannableString(nomorHp).apply {
                            setSpan(UnderlineSpan(), 0, this.length, 0)
                        }

                        cellViewHolder.tvCell.text = underlineText
                    }
                }
                NAMA, KETERANGAN -> cellViewHolder.tvCell.gravity = Gravity.START
            }
        }
    }

    override suspend fun getColumnHeaderItems(): List<ColumnHeader> {
        val columnHeaders = mutableListOf<DuColumnHeader>()
        columnHeaders.apply {
            add(NAMA, DuColumnHeader("Nama"))
            add(TANGGAL, DuColumnHeader("Tanggal"))
            add(NO_HP, DuColumnHeader("No. Hp"))
            add(TIKTOK, DuColumnHeader("Tiktok"))
            add(LOKASI_INDO, DuColumnHeader("Lok. Indo"))
            add(NEGARA_BEKERJA, DuColumnHeader("Neg. Bekerja"))
            add(KETERANGAN, DuColumnHeader("Keterangan"))
        }

        return columnHeaders
    }

    override suspend fun getRowHeaderItems(): List<RowHeader> {
        val rowHeaders = mutableListOf<DuRowHeader>()
        repeat(users.size) {
            rowHeaders.add(DuRowHeader(it.plus(1)))
        }

        return rowHeaders
    }

    override suspend fun getCellItems(): List<List<CellItem>> {
        val cellItems = mutableListOf<List<DuCellItem>>()
        users.forEach {
            val cell = mutableListOf<DuCellItem>()
            cell.apply {
                add(NAMA, DuCellItem(it.nama))
                add(TANGGAL, DuCellItem(it.tanggal.toSlashedString()))
                add(NO_HP, DuCellItem(it.noHp))
                add(TIKTOK, DuCellItem(it.usernameTiktok))
                add(LOKASI_INDO, DuCellItem(it.lokasiIndo))
                add(NEGARA_BEKERJA, DuCellItem(it.negaraBekerja))
                add(KETERANGAN, DuCellItem(it.keterangan))
            }

            cellItems.add(cell)
        }

        return cellItems
    }


    companion object {
        const val NAMA = 0
        const val TANGGAL = 1
        const val NO_HP = 2
        const val TIKTOK = 3
        const val LOKASI_INDO = 4
        const val NEGARA_BEKERJA = 5
        const val KETERANGAN = 6
    }

    data class DuColumnHeader(val mData: String): ColumnHeader {
        override fun getText(): String {
            return mData
        }
    }

    data class DuRowHeader(val nomor: Int): RowHeader {
        override fun getText(): String {
            return nomor.toString()
        }
    }

    data class DuCellItem(val mData: String): CellItem {
        override fun getText(): String {
            return mData
        }
    }
}