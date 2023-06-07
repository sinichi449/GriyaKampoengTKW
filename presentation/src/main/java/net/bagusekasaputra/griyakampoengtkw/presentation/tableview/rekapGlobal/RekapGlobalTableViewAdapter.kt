package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.rekapGlobal

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.TypefaceCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.evrencoskun.tableview.sort.SortState
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapCellViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapColumnHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableRekapRowHeaderBinding
import java.util.Date

object RekapGlobalColumnPosition {
    const val NAMA = 0
    const val TANGGAL_PEMBELIAN  = 1
    const val HARGA = 2
    const val JUMLAH_UANG_MASUK = 3
    const val SISA_PEMBAYARAN = 4
    const val PERSENTASE = 5
}

class RekapGlobalTableViewAdapter(
    private val onSortingStateReset: () -> Unit,
): AbstractTableAdapter<RgColumnHeader, RgRowHeader, RgCell>() {

    /**
     * Cell
     */
    class RgCellViewHolder(binding: TableRekapCellViewBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val tvCell = binding.tvCellData
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

        val typeface = if (columnPosition == RekapGlobalColumnPosition.PERSENTASE)
            Typeface.MONOSPACE else Typeface.SERIF
        viewHolder.tvCell.typeface = TypefaceCompat.create(
            holder.container.context,
            typeface,
            Typeface.NORMAL
        )

        if (columnPosition == RekapGlobalColumnPosition.NAMA) {
            viewHolder.tvCell.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        } else {
            viewHolder.tvCell.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        viewHolder.tvCell.text = when (columnPosition) {
            RekapGlobalColumnPosition.PERSENTASE -> {
                val persentaseDouble = cellItemModel?.data as Double?
                if (persentaseDouble != null) {
                    "${persentaseDouble}%"
                } else {
                    "-"
                }
            }

            RekapGlobalColumnPosition.TANGGAL_PEMBELIAN -> {
                val tanggalPembelian = cellItemModel?.data as Date?

                tanggalPembelian?.toSlashedString() ?: "-"
            }

            RekapGlobalColumnPosition.NAMA -> {
                (cellItemModel?.data as String?) ?: "-"
            }

            RekapGlobalColumnPosition.HARGA,
            RekapGlobalColumnPosition.SISA_PEMBAYARAN,
            RekapGlobalColumnPosition.JUMLAH_UANG_MASUK, -> {
                NumberUtil.formatLongToString(cellItemModel?.data as Long? ?: 0L)
            }

            else -> "N/A"
        }

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.tvCell.requestLayout()
    }

    /**
     * Column Header
     */
    class RgColumnHeaderViewHolder(binding: TableRekapColumnHeaderBinding): AbstractSorterViewHolder(binding.root) {
        val container = binding.root
        val text = binding.tvChData
        val imgSortingState = binding.imgSortingState

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

        override fun onSortingStatusChanged(sortState: SortState) {
            super.onSortingStatusChanged(sortState)

            val containerBackgroundColor: Int
            val iconSortingVisibility: Int
            val iconSortingDrawable: Int?
            when (sortState) {
                SortState.ASCENDING -> {
                    iconSortingVisibility = View.VISIBLE
                    containerBackgroundColor = R.color.secondaryColor
                    iconSortingDrawable = R.drawable.baseline_arrow_drop_up_24
                }
                SortState.DESCENDING -> {
                    iconSortingVisibility = View.VISIBLE
                    containerBackgroundColor = R.color.secondaryColor
                    iconSortingDrawable = R.drawable.baseline_arrow_drop_down_24
                }
                SortState.UNSORTED -> {
                    iconSortingVisibility = View.GONE
                    containerBackgroundColor = R.color.abang
                    iconSortingDrawable = null
                }
            }

            imgSortingState.visibility = iconSortingVisibility
            iconSortingDrawable?.also {
                imgSortingState.setImageDrawable(ContextCompat.getDrawable(
                    container.context, it
                ))
            }
            container.setBackgroundColor(ContextCompat.getColor(
                container.context, containerBackgroundColor
            ))
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
                binding.root.setOnClickListener {
                    onSortingStateReset()
                }

                binding.root
            }
        }
    }
}