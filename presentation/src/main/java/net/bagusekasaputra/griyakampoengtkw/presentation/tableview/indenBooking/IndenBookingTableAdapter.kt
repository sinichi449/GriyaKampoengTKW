package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.indenBooking

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableIndenBookingCellViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableIndenBookingColumnHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableIndenBookingCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableIndenBookingRowHeaderBinding

class IndenBookingTableAdapter: AbstractTableAdapter<TableIndenBooking.IbColumnHeader, TableIndenBooking.IbRowHeader, TableIndenBooking.IbCell>() {

    class IbCellViewHolder(binding: TableIndenBookingCellViewBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val tvCell = binding.tvCellData
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableIndenBookingCellViewBinding.inflate(inflater, parent, false).let { binding ->
                IbCellViewHolder(binding)
            }
        }
    }


    class IbColumnHeaderViewHolder(binding: TableIndenBookingColumnHeaderBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val tvColumnHeader = binding.tvChData

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (!isSelected) {
                container.setBackgroundColor(ContextCompat.getColor(
                    container.context, R.color.abang
                ))
                tvColumnHeader.setTextColor(ContextCompat.getColor(
                    container.context, R.color.white
                ))
            }
        }
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableIndenBookingColumnHeaderBinding.inflate(inflater, parent, false).let { binding ->
                IbColumnHeaderViewHolder(binding)
            }
        }
    }


    class IbRowHeaderViewHolder(binding: TableIndenBookingRowHeaderBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val tvRowHeader = binding.tvRhNomor
        var colorId = R.color.white

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (selectionState != SelectionState.SELECTED) {
                setBackgroundColor(ContextCompat.getColor(container.context, colorId))
            }
        }
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableIndenBookingRowHeaderBinding.inflate(inflater, parent, false).let { binding ->
                IbRowHeaderViewHolder(binding)
            }
        }
    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableIndenBookingCornerViewBinding.inflate(inflater, parent, false).let { binding ->
                binding.root
            }
        }
    }



    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: TableIndenBooking.IbRowHeader?,
        rowPosition: Int
    ) {
        val viewHolder = holder as IbRowHeaderViewHolder

        if (rowHeaderItemModel?.sudahIsiFoto == true) {
            viewHolder.colorId = com.evrencoskun.tableview.R.color.table_view_default_selected_background_color
        }

        viewHolder.tvRowHeader.text = rowHeaderItemModel?.nomor?.toString() ?: "0"
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: TableIndenBooking.IbColumnHeader?,
        columnPosition: Int
    ) {
        val viewHolder = holder as IbColumnHeaderViewHolder

        viewHolder.tvColumnHeader.text = columnHeaderItemModel?.text ?: "-"

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.tvColumnHeader.requestLayout()
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: TableIndenBooking.IbCell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val viewHolder = holder as IbCellViewHolder

        viewHolder.tvCell.text = cellItemModel?.text ?: "N/A"

        if (columnPosition == TableIndenBooking.COLUMN_NAMA_COSTUMER
            || columnPosition == TableIndenBooking.COLUMN_KETERANGAN) {
            viewHolder.tvCell.gravity = Gravity.START
        } else {
            viewHolder.tvCell.gravity = Gravity.CENTER
        }

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.tvCell.requestLayout()
    }
}