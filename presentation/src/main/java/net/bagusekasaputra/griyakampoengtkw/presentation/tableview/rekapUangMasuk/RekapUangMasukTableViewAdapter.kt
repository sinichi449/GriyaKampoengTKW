package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapUangMasuk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapUangMasukCellBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapUangMasukColumnHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapUangMasukCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapUangMasukRowHeaderBinding

class RekapUangMasukTableViewAdapter: AbstractTableAdapter<RumColumnHeader, RumRowHeader, RumCell>() {

    /**
     * Cell
     */
    private class RumCellViewHolder(binding: TableRekapUangMasukCellBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val cellText = binding.tvTumCell
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableRekapUangMasukCellBinding.inflate(layoutInflater, parent, false).let { binding ->
                RumCellViewHolder(binding)
            }
        }
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: RumCell?,
        columnPosition: Int,
        rowPosition: Int,
    ) {
        val viewHolder = holder as RumCellViewHolder

        viewHolder.cellText.text = cellItemModel?.text ?: "-"

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.cellText.requestLayout()
    }


    /**
     * Column Header
     */

    private class RumColumnHeaderViewHolder(binding: TableRekapUangMasukColumnHeaderBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val columnHeaderText = binding.tvTumColumnHeader

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (isSelected.not()) {
                container.setBackgroundColor(
                    ContextCompat.getColor(container.context, R.color.abang)
                )
                columnHeaderText.setTextColor(
                    ContextCompat.getColor(container.context, R.color.white)
                )
            }
        }
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableRekapUangMasukColumnHeaderBinding.inflate(layoutInflater, parent, false).let { binding ->
                RumColumnHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: RumColumnHeader?,
        columnPosition: Int,
    ) {
        val viewHolder = holder as RumColumnHeaderViewHolder

        viewHolder.columnHeaderText.text = columnHeaderItemModel?.text ?: "-"

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.columnHeaderText.requestLayout()
    }


    /**
     * Row Header
     */

    private class RumRowHeaderViewHolder(binding: TableRekapUangMasukRowHeaderBinding): AbstractViewHolder(binding.root) {
        val tvNomor = binding.tvTumRowHeaderNomor
        val tvKavling = binding.tvTumRowHeaderKavling
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableRekapUangMasukRowHeaderBinding.inflate(layoutInflater, parent, false).let { binding ->
                RumRowHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RumRowHeader?,
        rowPosition: Int,
    ) {
        val viewHolder = holder as RumRowHeaderViewHolder

        viewHolder.tvNomor.text = rowHeaderItemModel?.nomor ?: "-"
        viewHolder.tvKavling.text = rowHeaderItemModel?.kavling ?: "-"
    }


    /**
     * Corner
     */

    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableRekapUangMasukCornerViewBinding.inflate(layoutInflater, parent, false).let { binding ->
                binding.root
            }
        }
    }
}