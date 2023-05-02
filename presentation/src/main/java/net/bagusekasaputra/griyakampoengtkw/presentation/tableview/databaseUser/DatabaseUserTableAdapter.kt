package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.databaseUser

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableDatabaseUserCellViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableDatabaseUserColumnHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableDatabaseUserCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableDatabaseUserRowHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.databaseUser.TableDatabaseUser.CalonPembeliCell
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.databaseUser.TableDatabaseUser.CalonPembeliColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.databaseUser.TableDatabaseUser.CalonPembeliRowHeader


class DatabaseUserTableAdapter(): AbstractTableAdapter<CalonPembeliColumnHeader, CalonPembeliRowHeader, CalonPembeliCell>() {


    /**
     * Cell
     */
    private class CalonPembeliCellViewHolder(binding: TableDatabaseUserCellViewBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val tvCell = binding.tvCellData
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val binding = LayoutInflater.from(parent.context).let { inflater ->
            TableDatabaseUserCellViewBinding.inflate(inflater, parent, false)
        }
        return CalonPembeliCellViewHolder(binding)
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: CalonPembeliCell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val viewHolder = holder as CalonPembeliCellViewHolder

        viewHolder.tvCell.text = cellItemModel?.text ?: "-".run {
            // Underline only "No Hp" cells (column 1)
            if (columnPosition == 1) {
                val content = SpannableString(this)
                content.setSpan(UnderlineSpan(), 0, this.length, 0)

                content
            } else {
                this
            }
        }

        if (columnPosition == TableDatabaseUser.COLUMN_KETERANGAN ||
                columnPosition == TableDatabaseUser.COLUMN_NAMA) {
            viewHolder.tvCell.gravity = Gravity.START
        } else {
            viewHolder.tvCell.gravity = Gravity.CENTER
        }

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.tvCell.requestLayout()
    }


    /**
     * Column Header
     */
    class CalonPembeliColumnHeaderViewHolder(binding: TableDatabaseUserColumnHeaderBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val tvColumnHeader = binding.tvChData

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (isSelected.not()) {
                container.setBackgroundColor(
                    ContextCompat.getColor(container.context, R.color.abang)
                )
                tvColumnHeader.setTextColor(
                    ContextCompat.getColor(container.context, R.color.white)
                )
            }
        }
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        val binding = LayoutInflater.from(parent.context).let { inflater ->
            TableDatabaseUserColumnHeaderBinding.inflate(inflater, parent, false)
        }

        return CalonPembeliColumnHeaderViewHolder(binding)
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: CalonPembeliColumnHeader?,
        columnPosition: Int
    ) {
        val viewHolder = holder as CalonPembeliColumnHeaderViewHolder

        viewHolder.tvColumnHeader.text = columnHeaderItemModel?.text ?: "-"

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.tvColumnHeader.requestLayout()
    }


    /**
     * Row Header
     */
    class CalonPembeliRowHeaderViewHolder(itemBinding: TableDatabaseUserRowHeaderBinding): AbstractViewHolder(itemBinding.root) {
        val container = itemBinding.root
        val tvRowHeader = itemBinding.tvRhNomor
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val binding = LayoutInflater.from(parent.context).let { inflater ->
            TableDatabaseUserRowHeaderBinding.inflate(inflater, parent, false)
        }

        return CalonPembeliRowHeaderViewHolder(binding)
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: CalonPembeliRowHeader?,
        rowPosition: Int
    ) {
        val viewHolder = holder as CalonPembeliRowHeaderViewHolder

        viewHolder.tvRowHeader.text = rowHeaderItemModel?.nomor?.toString() ?: "0"
    }


    /**
     * Corner
     */
    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableDatabaseUserCornerViewBinding.inflate(inflater, parent, false).let { binding ->
                binding.root
            }
        }
    }
}