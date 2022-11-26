package net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutRecyclerJenisPembayaranBinding

class JenisBiayaMarketingRecyclerAdapter(
    private val jenisPembayaranList: List<String>,
    private val onJenisPembayaranClick: (position: Int) -> Unit,
): RecyclerView.Adapter<JenisBiayaMarketingRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: LayoutRecyclerJenisPembayaranBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return LayoutInflater.from(parent.context).let { layoutInflater ->
            LayoutRecyclerJenisPembayaranBinding.inflate(layoutInflater, parent, false).let { binding ->
                MyViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.btnJenisPembayaran.text = jenisPembayaranList[position]

        holder.binding.btnJenisPembayaran.setOnClickListener {
            onJenisPembayaranClick(position)
        }
    }

    override fun getItemCount(): Int {
        return jenisPembayaranList.size
    }


}