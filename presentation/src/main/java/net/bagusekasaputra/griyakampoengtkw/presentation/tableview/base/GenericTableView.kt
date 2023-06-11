package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.TableView
import com.evrencoskun.tableview.listener.ITableViewListener
import com.evrencoskun.tableview.sort.SortState

class GenericTableView<T>(
    private val tableView: TableView,
    private val dataSets: Collection<T>,
): ITableViewListener, TableViewHolderListener {
    private var columnHeaders: List<ColumnHeader> = emptyList()
    private var rowHeaders: List<RowHeader> = emptyList()
    private var cellItems: List<List<CellItem>> = emptyList()

    private var dataProvider: TableViewDataProvider<T>? = null

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
    private var onCornerViewBinding: ((cornerView: View) -> Unit)? = null

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

    var tableAdapter: DefaultTableViewAdapter? = null
        private set


    fun create() {
        with(tableView) {
            tableAdapter = DefaultTableViewAdapter(
                configurator = doubleRowHeaderConfigurator,
                tableViewHolderListener = this@GenericTableView,
            )

            setAdapter(tableAdapter)

            if (dataProvider != null) {
                tableAdapter?.updateData(dataProvider!!, dataSets)
            } else {
                tableAdapter?.setAllItems(columnHeaders, rowHeaders, cellItems)
            }
            tableAdapter?.notifyDataSetChanged()

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

    fun updateData(newData: Collection<T>) {
        if (dataProvider != null) {
            tableAdapter?.updateData(dataProvider!!, newData)
        }
    }

    fun setDataProvider(dataProvider: TableViewDataProvider<T>): GenericTableView<T> {
        this.dataProvider = dataProvider

        // set all items
        this.columnHeaders = dataProvider.getColumnHeaders(dataSets)
        this.rowHeaders = dataProvider.getRowHeaders(dataSets)
        this.cellItems = dataProvider.getCellItems(dataSets)

        return this
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

    fun setOnCellBinding(onBind: (cellViewHolder: CellViewHolder, cellItem: CellItem?, col: Int, row: Int, ) -> Unit, ): GenericTableView<T> {
        onCellBinding = onBind

        return this
    }

    fun setOnColumnHeaderBinding(onBind: (viewHolder: ColumnHeaderViewHolder, item: ColumnHeader?, column: Int) -> Unit): GenericTableView<T> {
        this.onColumnHeaderBinding = onBind

        return this
    }

    fun setOnRowHeaderBinding(onBind: (viewHolder: RowHeaderViewHolder, item: RowHeader?, row: Int) -> Unit): GenericTableView<T> {
        this.onRowHeaderBinding = onBind

        return this
    }

    fun setOnCornerViewBinding(onBind: (cornerView: View) -> Unit): GenericTableView<T> {
        this.onCornerViewBinding = onBind

        return this
    }

    private fun resetTableSortingStatus() {
        repeat(columnHeaders.size) { column ->
            tableView.sortColumn(column, SortState.UNSORTED)
        }

        create()
    }


    /**
     * Table Binding Listeners
     */
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
        onCornerViewBinding?.let {
            it(view)
        }

        view.setOnClickListener {
            resetTableSortingStatus()

            onCornerViewClicked?.let { click ->
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
        with(tableView) {
            val nextSortState = when (getSortingStatus(column)) {
                SortState.UNSORTED -> SortState.ASCENDING
                SortState.ASCENDING -> SortState.DESCENDING
                SortState.DESCENDING -> SortState.UNSORTED
            }
            if (nextSortState != SortState.UNSORTED) {
                sortColumn(column, nextSortState)
            } else {
                resetTableSortingStatus()
            }
        }
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