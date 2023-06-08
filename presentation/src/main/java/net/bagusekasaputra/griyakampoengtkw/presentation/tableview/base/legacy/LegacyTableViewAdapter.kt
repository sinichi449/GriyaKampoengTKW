package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.legacy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericCellViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericColumnHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericDoubleCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericDoubleRowHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericSingleCornerViewBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.TableGenericSingleRowHeaderBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.legacy.LegacyTableWrapper.LegacyCellItem
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.legacy.LegacyTableWrapper.LegacyColumnHeader
import net.bagusekasaputra.griyakampoengtkw.presentation.tableview.base.legacy.LegacyTableWrapper.LegacyRowHeader

class LegacyTableViewAdapter(
    private val doubleRowHeaderConfig: DoubleRowHeaderConfiguration? = null,
    private val additionalCellActions: (cellViewHolder: MyCellViewHolder, cellItem: LegacyCellItem?, column: Int, row: Int) -> Unit,
    private val additionalRowHeaderActions: (rowHeaderViewHolder: AbstractViewHolder, rowHeaderItem: LegacyRowHeader?, rowPosition: Int) -> Unit,
    private val additionalColumnHeaderActions: (columnHeaderViewHolder: MyColumnHeaderViewHolder, columnHeaderItem: LegacyColumnHeader?, columnPosition: Int) -> Unit,
    private val additionalCornerViewActions: (view: View, text: TextView) -> Unit,
): AbstractTableAdapter<LegacyColumnHeader, LegacyRowHeader, LegacyCellItem>() {

    data class DoubleRowHeaderConfiguration(
        val cornerTitle: String,
        val cornerTextSeparator: String,
    )

    class MyCellViewHolder(cellBinding: TableGenericCellViewBinding): AbstractViewHolder(cellBinding.root) {
        val container = cellBinding.root
        val tvCell = cellBinding.tvCellData
        var cellBackgroundColor = R.color.white
        var cellTextColor = R.color.black

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (selectionState != SelectionState.SELECTED) {
                val color = { resId: Int ->
                    ContextCompat.getColor(container.context, resId)
                }

                container.setBackgroundColor(color(cellBackgroundColor))
                tvCell.setTextColor(color(cellTextColor))
            }
        }
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val cellBinding = TableGenericCellViewBinding.inflate(
            getLayoutInflater(parent),
            parent, false,
        )

        return MyCellViewHolder(cellBinding)
    }


    class MyColumnHeaderViewHolder(colHeaderBinding: TableGenericColumnHeaderBinding): AbstractViewHolder(colHeaderBinding.root) {
        val container = colHeaderBinding.root
        val tvColumnHeader = colHeaderBinding.tvChData
        var containerBackground = R.color.abang
        var textColumnHeaderColor = R.color.white

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (selectionState != SelectionState.SELECTED) {
                val color = { resId: Int ->
                    ContextCompat.getColor(container.context, resId)
                }

                container.setBackgroundColor(color(containerBackground))
                tvColumnHeader.setTextColor(color(textColumnHeaderColor))
            }
        }
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        val colHeaderBinding = TableGenericColumnHeaderBinding.inflate(
            getLayoutInflater(parent), parent, false
        )

        return MyColumnHeaderViewHolder(colHeaderBinding)
    }


    class MySingleRowHeaderViewHolder(singleRowHeaderBinding: TableGenericSingleRowHeaderBinding): AbstractViewHolder(singleRowHeaderBinding.root) {
        val container = singleRowHeaderBinding.root
        val tvRowHeader = singleRowHeaderBinding.tvRhNomor
        var containerBackground = R.color.white
        var rowHeaderTextColor = R.color.black

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (selectionState != SelectionState.SELECTED) {
                val color = { resId: Int ->
                    ContextCompat.getColor(container.context, resId)
                }

                container.setBackgroundColor(color(containerBackground))
                tvRowHeader.setTextColor(color(rowHeaderTextColor))
            }
        }
    }

    class MyDoubleRowHeaderViewHolder(doubleRowHeaderBinding: TableGenericDoubleRowHeaderBinding): AbstractViewHolder(doubleRowHeaderBinding.root) {
        val container = doubleRowHeaderBinding.root
        val tvNomor = doubleRowHeaderBinding.tvRhNomor
        val tvData = doubleRowHeaderBinding.tvRhData
        var containerBackground = R.color.abang
        var rowHeadersTextColor = R.color.white

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (selectionState != SelectionState.SELECTED) {
                val color = { resId: Int ->
                    ContextCompat.getColor(container.context, resId)
                }

                container.setBackgroundColor(color(containerBackground))
                tvNomor.setTextColor(color(rowHeadersTextColor))
                tvData.setTextColor(color(rowHeadersTextColor))
            }
        }
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return if (doubleRowHeaderConfig != null) {
            val doubleRowHeader = TableGenericDoubleRowHeaderBinding.inflate(
                getLayoutInflater(parent), parent, false
            )

            MyDoubleRowHeaderViewHolder(doubleRowHeader)
        } else {
            val singleRowHeader = TableGenericSingleRowHeaderBinding.inflate(
                getLayoutInflater(parent), parent, false
            )

            MySingleRowHeaderViewHolder(singleRowHeader)
        }
    }


    override fun onCreateCornerView(parent: ViewGroup): View {
        val cornerView: View
        val cornerText: TextView
        if (doubleRowHeaderConfig != null) {
            val doubleCornerBinding = TableGenericDoubleCornerViewBinding.inflate(
                getLayoutInflater(parent), parent, false
            )

            cornerView = doubleCornerBinding.root
            cornerText = doubleCornerBinding.tvCornerTitle

            cornerText.text = doubleRowHeaderConfig.cornerTitle
        } else {
            val singleCornerBinding = TableGenericSingleCornerViewBinding.inflate(
                getLayoutInflater(parent), parent, false,
            )

            cornerView = singleCornerBinding.root
            cornerText = singleCornerBinding.tvCornerText
        }

        additionalCornerViewActions(cornerView, cornerText)

        return cornerView
    }


    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: LegacyRowHeader?,
        rowPosition: Int
    ) {
        if (doubleRowHeaderConfig != null) {
            val viewHolder = holder as MyDoubleRowHeaderViewHolder

            val separateNomorAndData = rowHeaderItemModel?.getText()
                ?.split(doubleRowHeaderConfig.cornerTextSeparator)
            val nomor = separateNomorAndData?.get(0)
            val mData = separateNomorAndData?.get(1)

            viewHolder.tvNomor.text = nomor ?: "N/A"
            viewHolder.tvData.text = mData ?: "N/A"

        } else {
            val viewHolder = holder as MySingleRowHeaderViewHolder

            viewHolder.tvRowHeader.text = rowHeaderItemModel?.getText() ?: "-"
        }

        additionalRowHeaderActions(holder, rowHeaderItemModel, rowPosition)
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: LegacyColumnHeader?,
        columnPosition: Int
    ) {
        val viewHolder = holder as MyColumnHeaderViewHolder

        viewHolder.tvColumnHeader.text = columnHeaderItemModel?.getText() ?: "NULL"

        additionalColumnHeaderActions(viewHolder, columnHeaderItemModel, columnPosition)

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.tvColumnHeader.requestLayout()
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: LegacyCellItem?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val viewHolder = holder as MyCellViewHolder

        viewHolder.tvCell.text = cellItemModel?.getText() ?: "N/A"

        additionalCellActions(viewHolder, cellItemModel, columnPosition, rowPosition)

        viewHolder.container.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        viewHolder.tvCell.requestLayout()
    }


    private fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
        return LayoutInflater.from(parent.context)
    }
}