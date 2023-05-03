package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaPribadi

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableBiayaPribadiCellViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableBiayaPribadiColumnHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableBiayaPribadiCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableBiayaPribadiRowHeaderBinding


class BiayaPribadiTableAdapter: AbstractTableAdapter<TableBiayaPribadi.BpColumnHeader, TableBiayaPribadi.BpRowHeader, TableBiayaPribadi.BpCell>() {

    /**
     * Cell
     */
    class BpCellViewHolder(binding: TableBiayaPribadiCellViewBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val tvCell = binding.tvCellData
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableBiayaPribadiCellViewBinding.inflate(inflater, parent, false).let { binding ->
                BpCellViewHolder(binding)
            }
        }
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: TableBiayaPribadi.BpCell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val viewHolder = holder as BpCellViewHolder

        viewHolder.tvCell.text = cellItemModel?.text ?: "N/A"

        if (columnPosition == TableBiayaPribadi.COLUMN_JENIS_BIAYA) {
            viewHolder.tvCell.gravity = Gravity.START
        } else {
            viewHolder.tvCell.gravity = Gravity.END
        }

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.tvCell.requestLayout()
    }


    /**
     * Column Header
     */
    class BpColumnHeaderViewHolder(binding: TableBiayaPribadiColumnHeaderBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val tvColumnHeader = binding.tvChData

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (!isSelected) {
                container.setBackgroundColor(ContextCompat.getColor(container.context, R.color.abang))
                tvColumnHeader.setTextColor(ContextCompat.getColor(container.context, R.color.white))
            }
        }
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableBiayaPribadiColumnHeaderBinding.inflate(inflater, parent, false).let { binding ->
                BpColumnHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: TableBiayaPribadi.BpColumnHeader?,
        columnPosition: Int
    ) {
        val viewHolder = holder as BpColumnHeaderViewHolder

        viewHolder.tvColumnHeader.text = columnHeaderItemModel?.text ?: "-"

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.tvColumnHeader.requestLayout()
    }


    /**
     * Row Header
     */
    class BpRowHeaderViewHolder(binding: TableBiayaPribadiRowHeaderBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val tvRowHeader = binding.tvRhNomor
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableBiayaPribadiRowHeaderBinding.inflate(inflater, parent, false).let { binding ->
                BpRowHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: TableBiayaPribadi.BpRowHeader?,
        rowPosition: Int
    ) {
        val viewHolder = holder as BpRowHeaderViewHolder

        viewHolder.tvRowHeader.text = rowHeaderItemModel?.nomor?.toString() ?: "0"
    }


    /**
     * Corner View
     */
    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableBiayaPribadiCornerViewBinding.inflate(inflater, parent, false).let { binding ->
                binding.root
            }
        }
    }
}