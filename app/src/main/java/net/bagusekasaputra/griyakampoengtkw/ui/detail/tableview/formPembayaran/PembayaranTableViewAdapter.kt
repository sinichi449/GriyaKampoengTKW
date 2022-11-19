package net.bagusekasaputra.griyakampoengtkw.ui.detail.tableview.formPembayaran

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.TablePembayaranCellLayoutBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.TablePembayaranColumnHeaderLayoutBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.TablePembayaranCornerLayoutBinding
import net.bagusekasaputra.griyakampoengtkw.databinding.TablePembayaranRowHeaderLayoutBinding

class PembayaranTableViewAdapter(): AbstractTableAdapter<PembayaranColumnHeader, PembayaranRowHeader, PembayaranCell>() {

    private object Kolom {
        const val TANGGAL = 0
        const val JUMLAH_UANG_DIBAYAR = 1
        const val TOTAL_UANG_MASUK = 2
        const val PERSENTASE = 3
        const val KETERANGAN_PROSES = 4
    }

    /**
     * Cell
     */
    class PembayaranCellViewHolder(binding: TablePembayaranCellLayoutBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val content = binding.tvCell
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TablePembayaranCellLayoutBinding.inflate(layoutInflater, parent, false).let { binding ->
                PembayaranCellViewHolder(binding)
            }
        }
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: PembayaranCell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val viewHolder = holder as PembayaranCellViewHolder

        viewHolder.content.text = cellItemModel?.mData as String? ?: "-"

        // Setting text alignment
        val alignEnd = { viewHolder.content.textAlignment = View.TEXT_ALIGNMENT_VIEW_END }
        val alignStart = { viewHolder.content.textAlignment = View.TEXT_ALIGNMENT_VIEW_START }
        val alignCenter = { viewHolder.content.textAlignment = View.TEXT_ALIGNMENT_CENTER }

        when (columnPosition) {
//            Kolom.TANGGAL, Kolom.PERSENTASE ->  alignCenter()
            Kolom.JUMLAH_UANG_DIBAYAR, Kolom.TOTAL_UANG_MASUK -> {
//                alignEnd()
                viewHolder.content.typeface = Typeface.SERIF
            }
            Kolom.KETERANGAN_PROSES -> alignStart()
        }

        viewHolder.container.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        viewHolder.content.requestLayout()
    }


    /**
     * Column Header
     */
    class PembayaranColumnHeaderViewHolder(val binding: TablePembayaranColumnHeaderLayoutBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val content = binding.tvColumnHeader

        // Fix background color can't be set
        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (isSelected.not()) {
                setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.abang))
                content.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            }
        }
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TablePembayaranColumnHeaderLayoutBinding.inflate(layoutInflater, parent, false).let { binding ->
                PembayaranColumnHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: PembayaranColumnHeader?,
        columnPosition: Int
    ) {
        val viewHolder = holder as PembayaranColumnHeaderViewHolder

        viewHolder.content.text = columnHeaderItemModel?.text ?: "N/A"


        viewHolder.container.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        viewHolder.content.requestLayout()
    }


    /**
     * Row Header
     */
    class PembayaranRowHeaderViewHolder(binding: TablePembayaranRowHeaderLayoutBinding): AbstractViewHolder(binding.root) {
        val container = binding.root
        val content = binding.tvRowHeader
        var colorId = R.color.white

        override fun setSelected(selectionState: SelectionState) {
            super.setSelected(selectionState)

            if (selectionState != SelectionState.SELECTED) {
                setBackgroundColor(
                    ContextCompat.getColor(container.context, colorId)
                )
            }
        }
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TablePembayaranRowHeaderLayoutBinding.inflate(layoutInflater, parent, false).let { binding ->
                PembayaranRowHeaderViewHolder(binding)
            }
        }
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: PembayaranRowHeader?,
        rowPosition: Int
    ) {
        val viewHolder = holder as PembayaranRowHeaderViewHolder

        // Set background color according to sudahIsiFoto
        // True -> set to yellow background
        // False -> default white
        val sudahIsiFoto = rowHeaderItemModel?.sudahIsiFoto ?: false
        val rowHeaderBackgroundColor = if (sudahIsiFoto)
            com.evrencoskun.tableview.R.color.table_view_default_selected_background_color
        else
            R.color.white
        viewHolder.colorId = rowHeaderBackgroundColor




        viewHolder.content.text = rowHeaderItemModel?.text ?: "N/A"
    }


    /**
     * Corner
     */
    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            TablePembayaranCornerLayoutBinding.inflate(layoutInflater, parent, false).let { binding ->
                binding.root
            }
        }
    }
}