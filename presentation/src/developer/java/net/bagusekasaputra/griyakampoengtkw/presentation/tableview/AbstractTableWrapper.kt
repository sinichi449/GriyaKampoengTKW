package net.bagusekasaputra.griyakampoengtkw.presentation.tableview

import com.evrencoskun.tableview.TableView
import com.evrencoskun.tableview.listener.ITableViewListener
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.GktTableViewAdapter.DoubleRowHeaderConfiguration

abstract class AbstractTableWrapper(
    private val tableView: TableView,
) {
    private var defaultTableListener: ITableViewListener? = null
    private var columnHeaderWidths: List<Pair<Int, Int>>? = null
    private var doubleRowHeaderConfig: DoubleRowHeaderConfiguration? = null

    abstract fun getColumnHeaderItems(): List<ColumnHeader>
    abstract fun getRowHeaderItems(): List<RowHeader>
    abstract fun getCellItems(): List<List<CellItem>>

    protected fun useDoubleCorner(cornerTitle: String, cornerSeparator: String): AbstractTableWrapper {
        doubleRowHeaderConfig = DoubleRowHeaderConfiguration(cornerTitle, cornerSeparator)

        return this
    }

    fun addWidthColumnHeader(columnAndWidths: List<Pair<Int, Int>>): AbstractTableWrapper {
        columnHeaderWidths = columnAndWidths

        return this
    }

    fun addTableListener(tableListener: ITableViewListener): AbstractTableWrapper {
        defaultTableListener = tableListener

        return this
    }

    fun createTable(additionalCellActions: (cellViewHolder: GktTableViewAdapter.MyCellViewHolder, cellItem: CellItem?, column: Int, row: Int) -> Unit = { _, _, _, _ -> }) {
        val adapter = GktTableViewAdapter(doubleRowHeaderConfig) { cellViewHolder, cellItem, column, row ->
            additionalCellActions(cellViewHolder, cellItem, column, row)
        }
        tableView.setAdapter(adapter)

        adapter.setAllItems(
            getColumnHeaderItems(),
            getRowHeaderItems(),
            getCellItems()
        )

        defaultTableListener?.also {
            tableView.tableViewListener = it
        }

        columnHeaderWidths?.forEach {
            tableView.setColumnWidth(it.first, it.second)
        }
    }

    interface ColumnHeader {
        fun getText(): String
    }

    interface RowHeader {
        fun getText(): String
    }

    interface CellItem {
        fun getText(): String
    }
}