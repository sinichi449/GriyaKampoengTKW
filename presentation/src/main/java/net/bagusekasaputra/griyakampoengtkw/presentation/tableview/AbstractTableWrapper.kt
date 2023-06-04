package net.bagusekasaputra.griyakampoengtkw.presentation.tableview

import android.view.View
import android.widget.TextView
import com.evrencoskun.tableview.TableView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.evrencoskun.tableview.listener.ITableViewListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.GktTableViewAdapter.DoubleRowHeaderConfiguration

abstract class AbstractTableWrapper(
    private val tableView: TableView,
) {
    private var defaultTableListener: ITableViewListener? = null
    private var columnHeaderWidths: List<Pair<Int, Int>>? = null
    private var doubleRowHeaderConfig: DoubleRowHeaderConfiguration? = null
    private var additionalCellActions: (cellViewHolder: GktTableViewAdapter.MyCellViewHolder, cellItem: CellItem?, column: Int, row: Int) -> Unit = { _, _, _, _ ->}
    private var additionalRowHeaderActions: (rowHeaderViewHolder: AbstractViewHolder, rowHeaderItem: RowHeader?, row: Int) -> Unit = { _, _, _ -> }
    private var additionalColumnHeaderActions: (columnHeaderViewHolder: GktTableViewAdapter.MyColumnHeaderViewHolder, columnHeaderItem: ColumnHeader?, columnPosition: Int) -> Unit = { _, _, _ -> }
    private var additionalCornerViewAction: (view: View, text: TextView) -> Unit = { _, _ -> }

    abstract suspend fun getColumnHeaderItems(): List<ColumnHeader>
    abstract suspend fun getRowHeaderItems(): List<RowHeader>
    abstract suspend fun getCellItems(): List<List<CellItem>>

    protected fun useDoubleCorner(cornerTitle: String, cornerSeparator: String): AbstractTableWrapper {
        doubleRowHeaderConfig = DoubleRowHeaderConfiguration(cornerTitle, cornerSeparator)

        return this
    }

    fun setWidthColumnHeader(columnAndWidths: List<Pair<Int, Int>>): AbstractTableWrapper {
        columnHeaderWidths = columnAndWidths

        return this
    }

    fun setTableListener(tableListener: ITableViewListener): AbstractTableWrapper {
        defaultTableListener = tableListener

        return this
    }

    fun setAdditionalCellActions(action: (cellViewHolder: GktTableViewAdapter.MyCellViewHolder, cellItem: CellItem?, column: Int, row: Int) -> Unit): AbstractTableWrapper {
        additionalCellActions = action

        return this
    }

    fun setAdditionalRowHeaderActions(action: (rowHeaderViewHolder: AbstractViewHolder, rowHeaderItem: RowHeader?, row: Int) -> Unit): AbstractTableWrapper {
        additionalRowHeaderActions = action

        return this
    }

    fun setAdditionalColumnHeaderActions(action: (columnHeaderViewHolder: GktTableViewAdapter.MyColumnHeaderViewHolder, columnHeaderItem: ColumnHeader?, columnPosition: Int) -> Unit): AbstractTableWrapper {
        additionalColumnHeaderActions = action

        return this
    }

    fun setAdditionalCornerViewActions(action: (view: View, text: TextView) -> Unit): AbstractTableWrapper {
        additionalCornerViewAction = action

        return this
    }

    fun createTable(
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
        onFinished: () -> Unit = {}
    ) {
        coroutineScope.launch(Dispatchers.Default) {
            val adapter = withContext(Dispatchers.Main) {
                val adapter = GktTableViewAdapter(
                    doubleRowHeaderConfig,
                    additionalCellActions,
                    additionalRowHeaderActions,
                    additionalColumnHeaderActions,
                    additionalCornerViewAction,
                )
                tableView.setAdapter(adapter)

                adapter
            }

            val columnHeaders = withContext(Dispatchers.Default) { getColumnHeaderItems() }
            val rowHeaders = withContext(Dispatchers.Default) { getRowHeaderItems() }
            val cellItems = withContext(Dispatchers.Default) { getCellItems() }

            withContext(Dispatchers.Main) {
                adapter.setAllItems(columnHeaders, rowHeaders, cellItems)

                defaultTableListener?.also {
                    tableView.tableViewListener = it
                }

                columnHeaderWidths?.forEach {
                    tableView.setColumnWidth(it.first, it.second)
                }

                onFinished()
            }
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