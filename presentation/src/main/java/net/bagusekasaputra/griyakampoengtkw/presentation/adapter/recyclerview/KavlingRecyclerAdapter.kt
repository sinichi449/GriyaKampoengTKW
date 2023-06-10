package net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.KavlingAndProgress
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutRecyclerKavlingsBinding


class KavlingRecyclerAdapter(
    private var progressList: List<KavlingAndProgress>,
    private val onRecyclerItemClick: (position: Int) -> Unit,
    private val onRecyclerItemHold: (anchor: View, position: Int) -> Unit,
): RecyclerView.Adapter<KavlingRecyclerAdapter.MyViewHolder>() {

    private lateinit var context: Context

    fun update(newList: List<KavlingAndProgress>) {
        val diffCallback = KavlingDataDiffCallback(progressList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.progressList = newList

        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(val binding: LayoutRecyclerKavlingsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        this.context = parent.context

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutRecyclerKavlingsBinding.inflate(
            layoutInflater, parent, false
        )

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val progress = progressList[position]
        val kavling = progress.kavling

        holder.binding.tvCardBlockName.text = kavling.kode
        holder.binding.tvTypeRumah.text = kavling.type
        holder.binding.cardKavling.isChecked = kavling.getSudahIsi()

        // Special case for Kavling C1 and C6
        holder.binding.tvUkuran.text =
            when (kavling.kode) {
                "C1" -> "67,12 m2"
                "C6" -> "63,63 m2"
                else -> kavling.ukuran
            }


        // Fill Layout progress settings
        val warna = Color.parseColor(kavling.warna)
        val persentase = progress.progress.persentaseBulanIni()

        holder.binding.cardKavling.setCardBackgroundColor(warna)
        holder.binding.fillProgressPersen.setProgressBackgroundColor(warna)
        holder.binding.layoutRoot.setBackgroundColor(warna)
        holder.binding.fillProgressPersen.setProgress(persentase, false)
        holder.binding.imgSudahBayarBulanIni.visibility = if (progress.progress.adaPembayaran)
            View.VISIBLE else View.GONE


        holder.binding.cardKavling.setOnClickListener {
            onRecyclerItemClick(position)
        }

        holder.binding.cardKavling.setOnLongClickListener {
            onRecyclerItemHold(it, position)
            true
        }
    }


    override fun getItemCount(): Int {
        return progressList.size
    }


    private fun clearAnimation(view: View) {
        view.clearAnimation()
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        clearAnimation(holder.binding.root)
        super.onViewDetachedFromWindow(holder)
    }
}

class KavlingDataDiffCallback(
    private val oldList: List<KavlingAndProgress>,
    private val newList: List<KavlingAndProgress>,
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.kavling == newItem.kavling
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.equals(newItem)
    }

}