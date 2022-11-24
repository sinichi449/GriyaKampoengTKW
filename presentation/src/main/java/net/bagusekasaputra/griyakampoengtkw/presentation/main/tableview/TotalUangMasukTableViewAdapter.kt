package net.bagusekasaputra.griyakampoengtkw.presentation.main.tableview

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableTotalUangMasukCellBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableTotalUangMasukColumnHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableTotalUangMasukCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableTotalUangMasukRowHeaderBinding

class TotalUangMasukTableViewAdapter: AbstractTableAdapter<TumColumnHeaders, TumRowHeaders, TumCell>() {

    /**
     * Cell
     */
    private class TumCellViewHolder(binding: TableTotalUangMasukCellBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val content = binding.tvTumCell
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableTotalUangMasukCellBinding.inflate(layoutInflater, parent, false).let { binding ->
                TumCellViewHolder(binding)
            }
        }
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: TumCell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val viewHolder = holder as TumCellViewHolder

        viewHolder.content.text = cellItemModel?.text ?: "-"
        viewHolder.content.typeface = Typeface.SERIF

        val cuanColumnPosition = 3
        if (columnPosition == cuanColumnPosition) {
            viewHolder.content.typeface = Typeface.DEFAULT_BOLD
        }

        // wrap content
        viewHolder.container.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        viewHolder.content.requestLayout()
    }


    /**
     * Column Header
     */
    private class TumColumnHeaderViewHolder(binding: TableTotalUangMasukColumnHeaderBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val content = binding.tvTumColumnHeader

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (selectionState != SelectionState.SELECTED) {
                container.setBackgroundColor(
                    ContextCompat.getColor(container.context, R.color.abang)
                )
                content.setTextColor(
                    ContextCompat.getColor(container.context, R.color.white)
                )
            }
        }
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableTotalUangMasukColumnHeaderBinding.inflate(layoutInflater, parent, false).let { binding ->
                TumColumnHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: TumColumnHeaders?,
        columnPosition: Int
    ) {
        val viewHolder = holder as TumColumnHeaderViewHolder

        viewHolder.content.text = columnHeaderItemModel?.text ?: "NO_DATA"

        viewHolder.container.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        viewHolder.content.requestLayout()
    }


    /**
     * Row Header
     */
    private class TumRowHeaderViewHolder(binding: TableTotalUangMasukRowHeaderBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val content = binding.tvTumRowHeader
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableTotalUangMasukRowHeaderBinding.inflate(layoutInflater, parent, false).let { binding ->
                TumRowHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: TumRowHeaders?,
        rowPosition: Int
    ) {
        val viewHolder = holder as TumRowHeaderViewHolder

        viewHolder.content.text = rowHeaderItemModel?.numStr ?: "-"
    }


    /**
     * Corner
     */
    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableTotalUangMasukCornerViewBinding.inflate(layoutInflater, parent, false).let { binding ->
                binding.root
            }
        }
    }
}