package net.bagusekasaputra.griyakampoengtkw.dataLama.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.dataLama.DataLamaModel
import net.bagusekasaputra.griyakampoengtkw.databinding.RecyclerItemDataLamaBinding

class DataLamaRecyclerAdapter(
    private val listDataLamaModel: List<DataLamaModel>,
    private val onItemClick: (position: Int) -> Unit,
    private val onDeleteAction: (position: Int) -> Unit,
): RecyclerView.Adapter<DataLamaRecyclerAdapter.DataLamaViewHolder>() {

    class DataLamaViewHolder(itemBinding: RecyclerItemDataLamaBinding): RecyclerView.ViewHolder(itemBinding.root) {
        val container = itemBinding.root
        val tvFilename = itemBinding.tvFilename
        val imgDelete = itemBinding.imgDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataLamaViewHolder {
        return LayoutInflater.from(parent.context).let { inflater ->
            DataLamaViewHolder(RecyclerItemDataLamaBinding.inflate(inflater, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return listDataLamaModel.size
    }

    override fun onBindViewHolder(holder: DataLamaViewHolder, position: Int) {
        val model = listDataLamaModel[position]

        holder.container.setOnClickListener {
            onItemClick(position)
        }
        holder.tvFilename.text = model.name
        holder.imgDelete.setOnClickListener {
            onDeleteAction(position)
        }
    }
}