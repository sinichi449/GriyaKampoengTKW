package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.legacy

import android.view.View
import android.widget.TextView
import com.evrencoskun.tableview.TableView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.evrencoskun.tableview.listener.ITableViewListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.legacy.LegacyTableViewAdapter.DoubleRowHeaderConfiguration

@Deprecated("Migrate to GenericTableAdapter")
abstract class LegacyTableWrapper(
    private val tableView: TableView,
) {
    private var defaultTableListener: ITableViewListener? = null
    private var columnHeaderWidths: List<Pair<Int, Int>>? = null
    private var doubleRowHeaderConfig: DoubleRowHeaderConfiguration? = null
    private var additionalCellActions: (cellViewHolder: LegacyTableViewAdapter.MyCellViewHolder, cellItem: LegacyCellItem?, column: Int, row: Int) -> Unit = { _, _, _, _ ->}
    private var additionalRowHeaderActions: (rowHeaderViewHolder: AbstractViewHolder, rowHeaderItem: LegacyRowHeader?, row: Int) -> Unit = { _, _, _ -> }
    private var additionalColumnHeaderActions: (columnHeaderViewHolder: LegacyTableViewAdapter.MyColumnHeaderViewHolder, columnHeaderItem: LegacyColumnHeader?, columnPosition: Int) -> Unit = { _, _, _ -> }
    private var additionalCornerViewAction: (view: View, text: TextView) -> Unit = { _, _ -> }

    abstract suspend fun getColumnHeaderItems(): List<LegacyColumnHeader>
    abstract suspend fun getRowHeaderItems(): List<LegacyRowHeader>
    abstract suspend fun getCellItems(): List<List<LegacyCellItem>>

    protected fun useDoubleCorner(cornerTitle: String, cornerSeparator: String): LegacyTableWrapper {
        doubleRowHeaderConfig = DoubleRowHeaderConfiguration(cornerTitle, cornerSeparator)

        return this
    }

    fun setWidthColumnHeader(columnAndWidths: List<Pair<Int, Int>>): LegacyTableWrapper {
        columnHeaderWidths = columnAndWidths

        return this
    }

    fun setTableListener(tableListener: ITableViewListener): LegacyTableWrapper {
        defaultTableListener = tableListener

        return this
    }

    fun setAdditionalCellActions(action: (cellViewHolder: LegacyTableViewAdapter.MyCellViewHolder, cellItem: LegacyCellItem?, column: Int, row: Int) -> Unit): LegacyTableWrapper {
        additionalCellActions = action

        return this
    }

    fun setAdditionalRowHeaderActions(action: (rowHeaderViewHolder: AbstractViewHolder, rowHeaderItem: LegacyRowHeader?, row: Int) -> Unit): LegacyTableWrapper {
        additionalRowHeaderActions = action

        return this
    }

    fun setAdditionalColumnHeaderActions(action: (columnHeaderViewHolder: LegacyTableViewAdapter.MyColumnHeaderViewHolder, columnHeaderItem: LegacyColumnHeader?, columnPosition: Int) -> Unit): LegacyTableWrapper {
        additionalColumnHeaderActions = action

        return this
    }

    fun setAdditionalCornerViewActions(action: (view: View, text: TextView) -> Unit): LegacyTableWrapper {
        additionalCornerViewAction = action

        return this
    }

    fun createTable(
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
        onFinished: () -> Unit = {}
    ) {
        coroutineScope.launch(Dispatchers.Default) {
            val adapter = withContext(Dispatchers.Main) {
                val adapter = LegacyTableViewAdapter(
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

    interface LegacyColumnHeader {
        fun getText(): String
    }

    interface LegacyRowHeader {
        fun getText(): String
    }

    interface LegacyCellItem {
        fun getText(): String
    }

    companion object {
        fun <T> createCellItems(
            collection: Collection<T>,
            sequentialActions: List<(T) -> LegacyCellItem>
        ): List<List<LegacyCellItem>> {
            return buildList {
                collection.forEach { item ->
                    val cells = mutableListOf<LegacyCellItem>()
                    sequentialActions.forEach { action ->
                        cells.add(action(item))
                    }
                    add(cells)
                }
            }
        }

    }

}