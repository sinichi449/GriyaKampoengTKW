package net.bagusekasaputra.griyakampoengtkw.presentation.main.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutRecyclerBlocksBinding

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

        holder.binding.cardBlock.setCardBackgroundColor(Color.parseColor(blocks[position].warna))

        holder.binding.cardBlock.setOnClickListener {
            onBlockClick(position)
        }
    }

    override fun getItemCount(): Int {
        return blocks.size
    }
}