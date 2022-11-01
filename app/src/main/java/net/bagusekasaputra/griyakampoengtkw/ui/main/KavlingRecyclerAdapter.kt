package net.bagusekasaputra.griyakampoengtkw.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.databinding.LayoutRecyclerKavlingsBinding
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling

class KavlingRecyclerAdapter(
    private val kavlings: List<Kavling>,
    private val onRecyclerItemClick: (position: Int) -> Unit
): RecyclerView.Adapter<KavlingRecyclerAdapter.MyViewHolder>() {

    private lateinit var context: Context

    class MyViewHolder(val binding: LayoutRecyclerKavlingsBinding)
        : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        this.context = parent.context

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutRecyclerKavlingsBinding.inflate(
            layoutInflater, parent, false
        )

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvCardBlockName.text = kavlings[position].kode
        holder.binding.tvUkuran.text = kavlings[position].ukuran
        holder.binding.tvTypeRumah.text = kavlings[position].type

        holder.binding.cardKavling.setCardBackgroundColor(
            getColor(kavlings[position].warna)
        )

        holder.binding.cardKavling.setOnClickListener {
            onRecyclerItemClick(position)
        }
    }

    private fun getColor(colorId: Int): Int {
        return context.resources.getColor(colorId)
    }

    override fun getItemCount(): Int {
        return kavlings.size
    }
}