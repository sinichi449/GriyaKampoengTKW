package net.bagusekasaputra.griyakampoengtkw.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.databinding.LayoutRecyclerKavlingsBinding
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling

class MainAdapter(
    private val kavlings: List<Kavling>,
    private val onRecyclerItemClick: (position: Int) -> Unit
): RecyclerView.Adapter<MainAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: LayoutRecyclerKavlingsBinding)
        : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutRecyclerKavlingsBinding.inflate(
            layoutInflater, parent, false
        )

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvCardBlockName.text = kavlings[position].kode

        holder.binding.cardBlock.setOnClickListener {
            onRecyclerItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return kavlings.size
    }
}