package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.TypefaceCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericCellViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericColumnHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericDoubleCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericDoubleRowHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericSingleCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericSingleRowHeaderBinding

class DefaultTableViewAdapter(
    private val configurator: DoubleRowHeaderConfigurator?,
    private val tableViewHolderListener: TableViewHolderListener,
): AbstractTableAdapter<ColumnHeader, RowHeader, CellItem>() {

    fun <T> updateData(dataProvider: TableViewDataProvider<T>, newData: Collection<T>) {
        setAllItems(
            dataProvider.getColumnHeaders(newData),
            dataProvider.getRowHeaders(newData),
            dataProvider.getCellItems(newData)
        )
    }

    /**
     * Cell
     */
    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val binding = TableGenericCellViewBinding.inflate(
            parent.layoutInflater(), parent, false
        )

        return CellViewHolder(binding)
    }

    /**
     * The cell text is default to convert [CellItem.data] into [String]. Typeface is set to
     * [Typeface.SERIF] and [Typeface.NORMAL].
     */
    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: CellItem?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val viewHolder = holder as CellViewHolder

        viewHolder.tvCell.text = cellItemModel?.data?.toString() ?: "-"
        viewHolder.tvCell.typeface = TypefaceCompat.create(
            holder.container.context,
            Typeface.SERIF, Typeface.NORMAL
        )

        tableViewHolderListener.onBindCellViewHolder(viewHolder, cellItemModel, columnPosition, rowPosition)
    }

    /**
     * Column Header
     */
    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        val binding = TableGenericColumnHeaderBinding.inflate(
            parent.layoutInflater(), parent, false
        )

        return ColumnHeaderViewHolder(binding)
    }

    /**
     * The column header's text is default to convert [ColumnHeader.text] into [String].
     */
    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: ColumnHeader?,
        columnPosition: Int
    ) {
        val viewHolder = holder as ColumnHeaderViewHolder

        viewHolder.tvColumnHeader.text = columnHeaderItemModel?.text ?: "-"

        tableViewHolderListener.onBindColumnHeaderViewHolder(viewHolder, columnHeaderItemModel, columnPosition)
    }

    /**
     * Row Header
     */
    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return if (configurator != null) {
            val binding = TableGenericDoubleRowHeaderBinding.inflate(
                parent.layoutInflater(), parent, false
            )
            DoubleRowHeaderViewHolder(binding, configurator)
        } else {
            val binding = TableGenericSingleRowHeaderBinding.inflate(
                parent.layoutInflater(), parent, false
            )
            SingleRowHeaderViewHolder(binding)
        }
    }

    /**
     * The row header's text is default to convert [RowHeader.text] into [String].
     */
    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RowHeader?,
        rowPosition: Int
    ) {
        val viewHolder = holder as RowHeaderViewHolder

        viewHolder.setRowHeaderText(rowHeaderItemModel?.text ?: "-")

        tableViewHolderListener.onBindRowHeaderViewHolder(viewHolder, rowHeaderItemModel, rowPosition)
    }

    /**
     * Corner View
     */
    override fun onCreateCornerView(parent: ViewGroup): View {
        val cornerView = if (configurator != null) {
            val binding = TableGenericDoubleCornerViewBinding.inflate(
                parent.layoutInflater(), parent, false
            )
            binding.tvCornerTitle.text = configurator.cornerViewTitle

            binding.root
        } else {
            val binding = TableGenericSingleCornerViewBinding.inflate(
                parent.layoutInflater(), parent, false
            )

            binding.root
        }

        tableViewHolderListener.onCreateCornerView(cornerView)

        return cornerView
    }

    private companion object {
        fun ViewGroup.layoutInflater(): LayoutInflater = LayoutInflater.from(this.context)
    }
}