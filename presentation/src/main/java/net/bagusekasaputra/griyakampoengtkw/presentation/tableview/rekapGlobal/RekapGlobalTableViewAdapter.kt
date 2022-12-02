package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.TypefaceCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapCellViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapColumnHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapRowHeaderBinding

object RekapGlobalColumnPosition {
    const val NAMA = 0
    const val TANGGAL_PEMBELIAN  = 1
    const val HARGA = 2
    const val JUMLAH_UANG_MASUK = 3
    const val SISA_PEMBAYARAN = 4
    const val PERSENTASE = 5
}

class RekapGlobalTableViewAdapter: AbstractTableAdapter<RgColumnHeader, RgRowHeader, RgCell>() {


    /**
     * Cell
     */
    class RgCellViewHolder(binding: TableRekapCellViewBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val text = binding.tvCellData
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableRekapCellViewBinding.inflate(inflater, parent, false).let { binding ->
                RgCellViewHolder(binding)
            }
        }
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: RgCell?,
        columnPosition: Int,
        rowPosition: Int,
    ) {
        val viewHolder = holder as RgCellViewHolder

        viewHolder.text.text = cellItemModel?.text ?: "-"
        viewHolder.text.typeface = TypefaceCompat.create(
            holder.container.context,
            Typeface.SERIF,
            Typeface.NORMAL
        )

        if (columnPosition == RekapGlobalColumnPosition.NAMA) {
            viewHolder.text.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        } else {
            viewHolder.text.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.text.requestLayout()
    }

    /**
     * Column Header
     */
    class RgColumnHeaderViewHolder(binding: TableRekapColumnHeaderBinding): AbstractViewHolder(binding.root) {
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
            TableRekapColumnHeaderBinding.inflate(inflater, parent, false).let { binding ->
                RgColumnHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: RgColumnHeader?,
        columnPosition: Int,
    ) {
        val viewHolder = holder as RgColumnHeaderViewHolder

        viewHolder.text.text = columnHeaderItemModel?.text ?: "-"

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.text.requestLayout()
    }


    /**
     * Row Header
     */
    class RgRowHeaderViewHolder(binding: TableRekapRowHeaderBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val tvNomor = binding.tvRhNomor
        val tvKavling = binding.tvRhKavling
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableRekapRowHeaderBinding.inflate(inflater, parent, false).let { binding ->
                RgRowHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RgRowHeader?,
        rowPosition: Int,
    ) {
        val viewHolder = holder as RgRowHeaderViewHolder

        viewHolder.tvNomor.text = rowHeaderItemModel?.nomor ?: "-"
        viewHolder.tvKavling.text = rowHeaderItemModel?.kavling ?: "-"
    }


    /**
     * Corner
     */
    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).let { inflater ->
            TableRekapCornerViewBinding.inflate(inflater, parent, false).let { binding ->
                binding.root
            }
        }
    }
}