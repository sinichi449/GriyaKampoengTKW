package net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressKavling
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutRecyclerKavlingsBinding

class KavlingRecyclerAdapter(
    private val kavlings: List<Kavling>,
    private val mapProgressKavling: Map<String, ProgressKavling>,
    private val onRecyclerItemClick: (position: Int) -> Unit,
    private val onRecyclerItemHold: (anchor: View, position: Int) -> Unit,
): RecyclerView.Adapter<KavlingRecyclerAdapter.MyViewHolder>() {

    private lateinit var context: Context

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
        holder.binding.tvCardBlockName.text = kavlings[position].kode
        holder.binding.tvUkuran.text = kavlings[position].ukuran
        holder.binding.tvTypeRumah.text = kavlings[position].type
//        holder.binding.imgSudahIsiDataDiri.visibility = if (kavlings[position].belumIsi)
//            View.GONE else View.VISIBLE
        holder.binding.cardKavling.isChecked = kavlings[position].getSudahIsi()

//        holder.binding.cardKavling.setCardBackgroundColor(Color.parseColor(kavlings[position].warna))

        holder.binding.imgSudahBayarBulanIni.visibility = if (kavlings[position].sudahBayarBulanIni)
            View.VISIBLE
        else
            View.GONE

        // Fill Layout progress settings
        val kavling = kavlings[position]
        val warna = Color.parseColor(kavling.warna)
        val progress = mapProgressKavling[kavling.kode]?.persentaseBulanIni()
        holder.binding.fillProgressPersen.setProgressColor(warna)
        if (progress != null) {
            holder.binding.fillProgressPersen.setProgress(progress, true)
        }


        holder.binding.cardKavling.setOnClickListener {
            onRecyclerItemClick(position)
        }

        holder.binding.cardKavling.setOnLongClickListener {
            onRecyclerItemHold(it, position)
            true
        }
//        setAnimation(holder.binding.root, position)
    }


    override fun getItemCount(): Int {
        return kavlings.size
    }


    private fun clearAnimation(view: View) {
        view.clearAnimation()
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        clearAnimation(holder.binding.root)
        super.onViewDetachedFromWindow(holder)
    }
}