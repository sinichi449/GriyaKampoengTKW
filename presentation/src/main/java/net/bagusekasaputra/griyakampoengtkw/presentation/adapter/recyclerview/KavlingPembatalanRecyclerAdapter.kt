package net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import net.bagusekasaputra.griyakampoengtkw.domain.entity.KavlingAndNama
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutRecyclerKavlingPembatalanBinding

class KavlingPembatalanRecyclerAdapter(
    private var kavlingAndNamaList: List<KavlingAndNama>,
    private val onKavlingClickListener: (
        kavlingView: MaterialCardView,
        position: Int,
    ) -> Unit,
): RecyclerView.Adapter<KavlingPembatalanRecyclerAdapter.MyViewHolder>() {

    private lateinit var context: Context

    class MyViewHolder(val binding: LayoutRecyclerKavlingPembatalanBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        this.context = parent.context

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutRecyclerKavlingPembatalanBinding.inflate(
            layoutInflater, parent, false
        )

        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return kavlingAndNamaList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val kavlingAndNama = kavlingAndNamaList[position]
        holder.binding.tvKavling.text = kavlingAndNama.kavlingKode
        holder.binding.tvNamaUser.text = kavlingAndNama.namaUser

        with(holder.binding) {
            cardKavling.setCardBackgroundColor(
                Color.parseColor(kavlingAndNama.warna)
            )
            layoutRoot.setBackgroundColor(
                Color.parseColor(kavlingAndNama.warna)
            )
        }

        holder.binding.cardKavling.setOnClickListener {
            onKavlingClickListener(holder.binding.cardKavling, position)
        }
    }

    fun update(newKavlingAndNamaList: List<KavlingAndNama>) {
        val diffCallback = KavlingPembatalanDiffCallback(kavlingAndNamaList, newKavlingAndNamaList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.kavlingAndNamaList = newKavlingAndNamaList

        diffResult.dispatchUpdatesTo(this)
    }
}

class KavlingPembatalanDiffCallback(
    private val oldList: List<KavlingAndNama>,
    private val newList: List<KavlingAndNama>,
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

        return oldItem.kavlingKode == newItem.kavlingKode
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem == newItem
    }

}