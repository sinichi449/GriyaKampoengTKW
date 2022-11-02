package net.bagusekasaputra.griyakampoengtkw.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.databinding.LayoutRecyclerBlocksBinding
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block

class BlockRecyclerAdapter(
    private val blocks: List<Block>,
    private val onBlockClick: (position: Int) -> Unit
): RecyclerView.Adapter<BlockRecyclerAdapter.MyViewHolder>() {

    private lateinit var context: Context

    class MyViewHolder(val binding: LayoutRecyclerBlocksBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        this.context = parent.context

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutRecyclerBlocksBinding.inflate(layoutInflater, parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvBlockKode.text = blocks[position].kode

        if (blocks[position].warna != null) {
            holder.binding.cardBlock.setCardBackgroundColor(
                getColor(blocks[position].warna!!)
            )
        } else {
            holder.binding.cardBlock.setCardBackgroundColor(
                getColor(R.color.black)
            )
        }

        holder.binding.cardBlock.setOnClickListener {
            onBlockClick(position)
        }
    }

    private fun getColor(colorId: Int): Int {
        return context.resources.getColor(colorId)
    }

    override fun getItemCount(): Int {
        return blocks.size
    }
}