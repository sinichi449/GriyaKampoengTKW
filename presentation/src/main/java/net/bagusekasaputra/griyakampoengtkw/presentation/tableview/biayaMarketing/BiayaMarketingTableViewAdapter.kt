package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaMarketing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableViewCellLayoutBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableViewColumnHeaderLayoutBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableViewRowHeaderLayoutBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableviewViewCornerLayoutBinding

class BiayaMarketingTableViewAdapter: AbstractTableAdapter<ColumnHeader, RowHeader, Cell>() {

    private val NO_DATA = "No Data"

    private object BiayaMarketingColumns {
        const val JenisPembayaran = 0
        const val Harga = 1
    }

    /**
     * Cell
     */
    private class MyCellViewHolder(binding: TableViewCellLayoutBinding): AbstractViewHolder(binding.root) {
        val cellContainer = binding.root
        val cellText = binding.tvCellData
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableViewCellLayoutBinding.inflate(layoutInflater, parent, false).let { binding ->
                MyCellViewHolder(binding)
            }
        }
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: Cell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val viewHolder = holder as MyCellViewHolder

        viewHolder.cellText.text = cellItemModel?.text ?: NO_DATA

        if (columnPosition == 0) {
            viewHolder.cellText.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }

        // remeasure for auto size cell & columns
        viewHolder.cellContainer.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        viewHolder.cellText.requestLayout()
    }


    /**
     * Column Header
     */
    private class MyColumnHeaderViewHolder(val binding: TableViewColumnHeaderLayoutBinding): AbstractViewHolder(binding.root) {
        val columnHeaderContainer = binding.root
        val columnHeaderText = binding.tvColumnHeader

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (isSelected.not()) {
                columnHeaderContainer.setBackgroundColor(
                    ContextCompat.getColor(columnHeaderContainer.context, R.color.abang)
                )
                columnHeaderText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            }
        }
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableViewColumnHeaderLayoutBinding.inflate(layoutInflater, parent, false).let { binding ->
                MyColumnHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: ColumnHeader?,
        columnPosition: Int
    ) {
        val viewHolder = holder as MyColumnHeaderViewHolder

        viewHolder.columnHeaderText.text = columnHeaderItemModel?.text ?: NO_DATA

        // remeasure for auto-sizing itself
        viewHolder.columnHeaderContainer.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        viewHolder.columnHeaderText.requestLayout()
    }


    /**
     * Row Header
     */
    private class MyRowHeaderViewHolder(binding: TableViewRowHeaderLayoutBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val rowHeaderText = binding.tvRowHeader
        var colorId = R.color.white

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (selectionState != SelectionState.SELECTED) {
                setBackgroundColor(
                    ContextCompat.getColor(container.context, colorId)
                )
            }
        }
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableViewRowHeaderLayoutBinding.inflate(layoutInflater, parent, false).let { binding ->
                MyRowHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RowHeader?,
        rowPosition: Int
    ) {
        val viewHolder = holder as MyRowHeaderViewHolder

        // the width of row header is soo small that it must be different text on null data
        viewHolder.rowHeaderText.text = rowHeaderItemModel?.text ?: "-"
    }


    /**
     * Corner
     */
    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableviewViewCornerLayoutBinding.inflate(layoutInflater, parent, false).let { binding ->
                binding.root
            }
        }
    }
}