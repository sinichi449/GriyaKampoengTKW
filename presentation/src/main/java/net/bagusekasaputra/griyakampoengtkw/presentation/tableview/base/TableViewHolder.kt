package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.evrencoskun.tableview.sort.SortState
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericCellViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericColumnHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericDoubleRowHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericSingleRowHeaderBinding

/**
 * Cell
 */
class CellViewHolder(binding: TableGenericCellViewBinding) : AbstractViewHolder(binding.root) {
    val container = binding.root
    val tvCell = binding.tvCellData
}

/**
 * Column Header
 */
class ColumnHeaderViewHolder(
    binding: TableGenericColumnHeaderBinding
): AbstractSorterViewHolder(binding.root) {
    val container = binding.root
    val tvColumnHeader = binding.tvChData
    private val imgSortingState = binding.imgSortingState

    // backgroundColor is clashing with JVM signature setBackgroundColor()
    var columnHeaderBackgroundColour = R.color.abang

    private val context = container.context

    override fun setSelected(selectionState: SelectionState) {
        super.setSelected(selectionState)

        if (!isSelected && !isShadowed) {
            setBackgroundColor(ContextCompat.getColor(context, columnHeaderBackgroundColour))
            tvColumnHeader.setTextColor(Color.WHITE)
        }
    }

    override fun onSortingStatusChanged(pSortState: SortState) {
        super.onSortingStatusChanged(pSortState)

        val imgSortingVisibility: Int
        val imgSortingResId: Int?

        when (pSortState) {
            SortState.ASCENDING -> {
                imgSortingVisibility = View.VISIBLE
                imgSortingResId = R.drawable.baseline_arrow_drop_up_24
            }
            SortState.DESCENDING -> {
                imgSortingVisibility = View.VISIBLE
                imgSortingResId = R.drawable.baseline_arrow_drop_down_24
            }
            SortState.UNSORTED -> {
                imgSortingVisibility = View.GONE
                imgSortingResId = null
            }
        }

        imgSortingState.apply {
            visibility = imgSortingVisibility
            imgSortingResId?.also {
                setImageDrawable(ContextCompat.getDrawable(context, it))
            }
        }
    }
}

/**
 * Row Header
 */
abstract class RowHeaderViewHolder(
    rowHeaderView: View,
    val configurator: DoubleRowHeaderConfigurator?,
): AbstractViewHolder(rowHeaderView) {

    abstract fun setRowHeaderText(text: String)
}

class SingleRowHeaderViewHolder(
    binding: TableGenericSingleRowHeaderBinding
): RowHeaderViewHolder(binding.root, null) {
    val container = binding.root
    val tvRowHeader = binding.tvRhNomor

    override fun setRowHeaderText(text: String) {
        tvRowHeader.text = text
    }
}

class DoubleRowHeaderViewHolder(
    binding: TableGenericDoubleRowHeaderBinding,
    configurator: DoubleRowHeaderConfigurator
): RowHeaderViewHolder(binding.root, configurator) {
    val container = binding.root
    val tvNomor = binding.tvRhNomor
    val tvRowHeader = binding.tvRhData

    override fun setRowHeaderText(text: String) {
        assert(configurator != null)

        val split = text.split(configurator!!.cornerTextSeparator)
        tvNomor.text = split[0]
        tvRowHeader.text = split[1]
    }
}


data class DoubleRowHeaderConfigurator(
    val cornerViewTitle: String,
    val cornerTextSeparator: String,
)

interface TableViewHolderListener {

    fun onBindCellViewHolder(
        holder: CellViewHolder,
        cellItemModel: CellItem?,
        columnPosition: Int,
        rowPosition: Int,
    )

    fun onBindColumnHeaderViewHolder(
        holder: ColumnHeaderViewHolder,
        columnHeaderItemModel: ColumnHeader?,
        columnPosition: Int
    )

    fun onBindRowHeaderViewHolder(
        holder: RowHeaderViewHolder,
        rowHeaderItemModel: RowHeader?,
        rowPosition: Int
    )

    fun onCreateCornerView(view: View)
}