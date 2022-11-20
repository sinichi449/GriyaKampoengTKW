package net.bagusekasaputra.griyakampoengtkw.presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutRecyclerTerminBinding

class TerminRecyclerAdapter(
    private val termins: List<String>,
    private val onTerminItemClick: (position: Int) -> Unit,
): RecyclerView.Adapter<TerminRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: LayoutRecyclerTerminBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutRecyclerTerminBinding.inflate(layoutInflater, parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.btnTermin.text = termins[position]

        holder.binding.btnTermin.setOnClickListener {
            onTerminItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return termins.size
    }
}