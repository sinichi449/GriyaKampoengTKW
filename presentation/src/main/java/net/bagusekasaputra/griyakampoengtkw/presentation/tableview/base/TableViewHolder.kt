package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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

    private val context = container.context

    var bgColour = R.color.table_cell_bg_colour
    var textColour = R.color.table_text_colour

    override fun setSelected(selectionState: SelectionState) {
        super.setSelected(selectionState)

        if (!isSelected && !isShadowed) {
            container.setBackgroundColor(
                ContextCompat.getColor(context, bgColour)
            )
            tvCell.setTextColor(
                ContextCompat.getColor(context, textColour)
            )
        }
    }
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
    var columnHeaderBackgroundColour = R.color.table_column_header_colour
    var columnHeaderTextColour = R.color.white

    private val context = container.context

    override fun setSelected(selectionState: SelectionState) {
        super.setSelected(selectionState)

        if (!isSelected && !isShadowed) {
            container.setBackgroundColor(
                ContextCompat.getColor(context, columnHeaderBackgroundColour)
            )
            tvColumnHeader.setTextColor(
                ContextCompat.getColor(context, columnHeaderTextColour)
            )
        }

        tvColumnHeader.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        tvColumnHeader.requestLayout()
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
    private val rowHeaderView: View,
    val configurator: DoubleRowHeaderConfigurator?,
): AbstractViewHolder(rowHeaderView) {
    private val context = rowHeaderView.context

    abstract fun setRowHeaderText(text: String)

    abstract fun getRowHeaderBgColour(): Int

    abstract fun getTextView(): TextView

    abstract fun getTextViewColour(): Int

    abstract fun setRowHeaderBgColour(colour: Int)

    abstract fun setTextViewColour(colour: Int)

    override fun setSelected(selectionState: SelectionState) {
        super.setSelected(selectionState)

        if (!isSelected && !isShadowed) {
            rowHeaderView.setBackgroundColor(
                ContextCompat.getColor(context, getRowHeaderBgColour())
            )
            getTextView().setTextColor(
                ContextCompat.getColor(context, getTextViewColour())
            )
        }
    }
}

class SingleRowHeaderViewHolder(
    binding: TableGenericSingleRowHeaderBinding
): RowHeaderViewHolder(binding.root, null) {
    val container = binding.root
    val tvRowHeader = binding.tvRhNomor

    var bgColour = R.color.table_unselected_colour
    var textColour = R.color.table_text_colour

    override fun setRowHeaderText(text: String) {
        tvRowHeader.text = text
    }

    override fun getRowHeaderBgColour(): Int {
        return bgColour
    }

    override fun getTextView(): TextView {
        return tvRowHeader
    }

    override fun getTextViewColour(): Int {
        return textColour
    }

    override fun setRowHeaderBgColour(colour: Int) {
        this.bgColour = colour
    }

    override fun setTextViewColour(colour: Int) {
        this.textColour = colour
    }
}

class DoubleRowHeaderViewHolder(
    binding: TableGenericDoubleRowHeaderBinding,
    configurator: DoubleRowHeaderConfigurator
): RowHeaderViewHolder(binding.root, configurator) {
    val container = binding.root
    val tvNomor = binding.tvRhNomor
    val tvRowHeader = binding.tvRhData

    var bgColour = R.color.table_unselected_colour
    var textColour = R.color.table_text_colour

    override fun setRowHeaderText(text: String) {
        assert(configurator != null)

        val split = text.split(configurator!!.cornerTextSeparator)
        tvNomor.text = split[0]
        tvRowHeader.text = split[1]
    }

    override fun getRowHeaderBgColour(): Int {
        return bgColour
    }

    override fun getTextView(): TextView {
        return tvRowHeader
    }

    override fun getTextViewColour(): Int {
        return textColour
    }

    override fun setRowHeaderBgColour(colour: Int) {
        this.bgColour = colour
    }

    override fun setTextViewColour(colour: Int) {
        this.textColour = colour
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