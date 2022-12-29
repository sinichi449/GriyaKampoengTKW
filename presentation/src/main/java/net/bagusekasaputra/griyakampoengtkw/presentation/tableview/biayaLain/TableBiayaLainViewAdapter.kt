package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaLain

import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableBiayaLainCellViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableBiayaLainColumnHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableBiayaLainCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableBiayaLainRowHeaderBinding

object BiayaLainColumnPosition {
    const val JENIS_BIAYA = 0
    const val HARGA = 1
    const val TANGGAL = 2
}

class TableBiayaLainViewAdapter: AbstractTableAdapter<BlColumnHeader, BlRowHeader, BlCell>() {

    /**
     * Cell
     */
    class BlCellViewHolder(binding: TableBiayaLainCellViewBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val text = binding.tvCellData
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableBiayaLainCellViewBinding.inflate(inflater, parent, false).let { binding ->
                BlCellViewHolder(binding)
            }
        }
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: BlCell?,
        columnPosition: Int,
        rowPosition: Int,
    ) {
        val viewHolder = holder as BlCellViewHolder

        viewHolder.text.text = cellItemModel?.text ?: "-"

        if (columnPosition == BiayaLainColumnPosition.JENIS_BIAYA) {
            viewHolder.text.gravity = Gravity.START
        } else {
            viewHolder.text.gravity = Gravity.CENTER
        }

        if (columnPosition == BiayaLainColumnPosition.HARGA) {
            viewHolder.text.typeface = Typeface.SERIF
        }

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.text.requestLayout()
    }


    /**
     * Column Header
     */
    class BlColumnHeaderViewHolder(binding: TableBiayaLainColumnHeaderBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val text = binding.tvChData

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (isSelected.not()) {
                container.setBackgroundColor(
                    ContextCompat.getColor(container.context, R.color.abang)
                )
                text.setTextColor(
                    ContextCompat.getColor(container.context, R.color.white)
                )
            }
        }
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableBiayaLainColumnHeaderBinding.inflate(inflater, parent, false).let { binding ->
                BlColumnHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: BlColumnHeader?,
        columnPosition: Int,
    ) {
        val viewHolder = holder as BlColumnHeaderViewHolder

        viewHolder.text.text = columnHeaderItemModel?.text ?: "-"

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.text.requestLayout()
    }


    /**
     * Row Header
     */
    class BlRowHeaderViewHolder(binding: TableBiayaLainRowHeaderBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val text = binding.tvRhNomor
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableBiayaLainRowHeaderBinding.inflate(inflater, parent, false).let { binding ->
                BlRowHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: BlRowHeader?,
        rowPosition: Int,
    ) {
        val viewHolder = holder as BlRowHeaderViewHolder

        viewHolder.text.text = rowHeaderItemModel?.nomor ?: "0"
    }


    /**
     * Corner
     */
    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableBiayaLainCornerViewBinding.inflate(inflater, parent, false).let { binding ->
                binding.root
            }
        }
    }
}