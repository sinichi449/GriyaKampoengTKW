package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.TableView
import com.evrencoskun.tableview.listener.ITableViewListener

class GenericTableView<T>(
    private val tableView: TableView,
    private val dataSets: Collection<T>,
): ITableViewListener, TableViewHolderListener {
    private var columnHeaders: List<ColumnHeader> = emptyList()
    private var rowHeaders: List<RowHeader> = emptyList()
    private var cellItems: List<List<CellItem>> = emptyList()

    private var onCellBinding: ((
        viewHolder: CellViewHolder,
        item: CellItem?,
        column: Int,
        row: Int
    ) -> Unit)? = null
    private var onRowHeaderBinding: ((
        viewHolder: RowHeaderViewHolder,
        item: RowHeader?,
        row: Int
    ) -> Unit)? = null
    private var onColumnHeaderBinding: ((
        viewHolder: ColumnHeaderViewHolder,
        item: ColumnHeader?,
        column: Int
    ) -> Unit)? = null
    private var onCreateCornerView: ((
        cornerView: View,
        tvTitle: TextView
    ) -> Unit)? = null

    private var onClickedCellItem: ((
        cellView: RecyclerView.ViewHolder,
        column: Int,
        row: Int
    ) -> Unit)? = null
    private var onClickedColumnHeader: ((
        columnHeaderView: RecyclerView.ViewHolder,
        column: Int
    ) -> Unit)? = null
    private var onClickedRowHeader: ((
        rowHeaderView: RecyclerView.ViewHolder,
        row: Int
    ) -> Unit)? = null
    private var onCornerViewClicked: ((view: View) -> Unit)? = null

    private var columnWidths: List<Pair<Int, Int>>? = null

    private var doubleRowHeaderConfigurator: DoubleRowHeaderConfigurator? = null


    fun create() {
        with(tableView) {
            val adapter = DefaultTableViewAdapter(
                configurator = doubleRowHeaderConfigurator,
                tableViewHolderListener = this@GenericTableView,
            )

            setAdapter(adapter)

            adapter.setAllItems(columnHeaders, rowHeaders, cellItems)
            adapter.notifyDataSetChanged()

            columnWidths?.forEach {
                setColumnWidth(it.first, it.second)
            }

            tableViewListener = this@GenericTableView

            selectionHandler.apply {
                selectedColumnPosition = -1
                selectedRowPosition = -1
            }

            scrollToRowPosition(0)
        }
    }


    fun buildColumnHeader(vararg title: String): GenericTableView<T> {
        columnHeaders = buildList {
            title.forEach { item ->
                add(ColumnHeader(item))
            }
        }

        return this
    }

    fun buildCellItems(
        sequentialActions: List<(item: T) -> CellItem>,
    ): GenericTableView<T> {
        cellItems = buildList {
            dataSets.forEach {  item ->
                val cells = mutableListOf<CellItem>()
                sequentialActions.forEach { action ->
                    cells.add(action(item))
                }
                add(cells)
            }
        }

        return this
    }

    fun buildRowHeader(
        action: (index: Int, item: T) -> RowHeader
    ): GenericTableView<T> {
        rowHeaders = buildList {
            dataSets.forEachIndexed { index, item ->
                add(action(index, item))
            }
        }

        return this
    }

    fun setWidthColumnHeaders(widths: List<Pair<Int, Int>>): GenericTableView<T> {
        this.columnWidths = widths

        return this
    }

    fun useDoubleCorner(configurator: DoubleRowHeaderConfigurator): GenericTableView<T> {
        this.doubleRowHeaderConfigurator = configurator

        return this
    }

    override fun onBindCellViewHolder(
        holder: CellViewHolder,
        cellItemModel: CellItem?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        onCellBinding?.let { it(holder, cellItemModel, columnPosition, rowPosition) }
    }

    override fun onBindColumnHeaderViewHolder(
        holder: ColumnHeaderViewHolder,
        columnHeaderItemModel: ColumnHeader?,
        columnPosition: Int
    ) {
        onColumnHeaderBinding?.let { it(holder, columnHeaderItemModel, columnPosition) }
    }

    override fun onBindRowHeaderViewHolder(
        holder: RowHeaderViewHolder,
        rowHeaderItemModel: RowHeader?,
        rowPosition: Int
    ) {
        onRowHeaderBinding?.let { it(holder, rowHeaderItemModel, rowPosition) }
    }

    override fun onCreateCornerView(view: View) {
        onCornerViewClicked?.let { click ->
            view.setOnClickListener {
                click(view)
            }
        }
    }

    /**
     * Table OnClick
     */
    override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
        onClickedCellItem?.let {
            it(cellView, column, row)
        }
    }

    override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
        onClickedRowHeader?.let {
            it(rowHeaderView, row)
        }
    }

    override fun onColumnHeaderClicked(columnHeaderView: RecyclerView.ViewHolder, column: Int) {
        onClickedColumnHeader?.let {
            it(columnHeaderView, column)
        }
    }

    /**
     * Table onDoubleClick
     */
    override fun onCellDoubleClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {

    }

    override fun onRowHeaderDoubleClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {

    }

    override fun onColumnHeaderDoubleClicked(
        columnHeaderView: RecyclerView.ViewHolder,
        column: Int
    ) {

    }

    /**
     * Table onLongPressed
     */
    override fun onCellLongPressed(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {

    }

    override fun onColumnHeaderLongPressed(columnHeaderView: RecyclerView.ViewHolder, column: Int) {

    }

    override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) {

    }

}