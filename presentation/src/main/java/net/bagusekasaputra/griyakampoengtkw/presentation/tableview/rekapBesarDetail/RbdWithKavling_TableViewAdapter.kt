package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapBesarDetail

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.TypefaceCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapBesarDetailCellBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapBesarDetailColumnHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapBesarDetailCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapBesarDetailRowHeaderBinding

/**
 * RBD = Rekap Besar Detail
 * This adapter is specifically made for the table which has 2 row headers,
 * that is [No, Kavling].
*/
class RbdWithKavling_TableViewAdapter(
    private val onCellTextCreated: (columnPosition: Int, cellTextView: TextView) -> Unit = { _, _ -> },
): AbstractTableAdapter<RbdColumnHeader, RbdWithKavlingRowHeader, RbdCell>() {

    /**
     * Cell
     */
    private class RbdWithKavling_CellViewHolder(binding: TableRekapBesarDetailCellBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val cellText = binding.tvTumCell
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableRekapBesarDetailCellBinding.inflate(layoutInflater, parent, false).let { binding ->
                RbdWithKavling_CellViewHolder(binding)
            }
        }
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: RbdCell?,
        columnPosition: Int,
        rowPosition: Int,
    ) {
        val viewHolder = holder as RbdWithKavling_CellViewHolder

        viewHolder.cellText.text = cellItemModel?.text ?: "-"

        viewHolder.cellText.typeface = TypefaceCompat.create(
            holder.container.context,
            Typeface.SERIF,
            Typeface.NORMAL
        )
        onCellTextCreated(columnPosition, viewHolder.cellText)

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.cellText.requestLayout()
    }


    /**
     * Column Header
     */

    private class RbdWithKavling_ColumnHeaderViewHolder(binding: TableRekapBesarDetailColumnHeaderBinding): AbstractViewHolder(binding.root) {
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
            TableRekapBesarDetailColumnHeaderBinding.inflate(layoutInflater, parent, false).let { binding ->
                RbdWithKavling_ColumnHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: RbdColumnHeader?,
        columnPosition: Int,
    ) {
        val viewHolder = holder as RbdWithKavling_ColumnHeaderViewHolder

        viewHolder.columnHeaderText.text = columnHeaderItemModel?.text ?: "-"

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.columnHeaderText.requestLayout()
    }


    /**
     * Row Header
     */

    private class RbdWithKavling_RowHeaderViewHolder(binding: TableRekapBesarDetailRowHeaderBinding): AbstractViewHolder(binding.root) {
        val tvNomor = binding.tvTumRowHeaderNomor
        val tvKavling = binding.tvTumRowHeaderKavling
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableRekapBesarDetailRowHeaderBinding.inflate(layoutInflater, parent, false).let { binding ->
                RbdWithKavling_RowHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RbdWithKavlingRowHeader?,
        rowPosition: Int,
    ) {
        val viewHolder = holder as RbdWithKavling_RowHeaderViewHolder

        viewHolder.tvNomor.text = rowHeaderItemModel?.nomor ?: "-"
        viewHolder.tvKavling.text = rowHeaderItemModel?.kavling ?: "-"
    }


    /**
     * Corner
     */

    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TableRekapBesarDetailCornerViewBinding.inflate(layoutInflater, parent, false).let { binding ->
                binding.root
            }
        }
    }
}