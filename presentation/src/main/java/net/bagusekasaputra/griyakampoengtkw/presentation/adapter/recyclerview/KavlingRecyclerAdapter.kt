package net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview

import android.content.Context
import android.graphics.Color
import android.util.Log
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
//        holder.binding.tvUkuran.text = kavlings[position].ukuran
        holder.binding.tvTypeRumah.text = kavlings[position].type
//        holder.binding.imgSudahIsiDataDiri.visibility = if (kavlings[position].belumIsi)
//            View.GONE else View.VISIBLE
        holder.binding.cardKavling.isChecked = kavlings[position].getSudahIsi()
//        holder.binding.cardKavling.setCardBackgroundColor(Color.parseColor(kavlings[position].warna))
        holder.binding.imgSudahBayarBulanIni.visibility = if (kavlings[position].sudahBayarBulanIni)
            View.VISIBLE
        else
            View.GONE

        // Special case for Kavling C1 and C6
        val kavling = kavlings[position]
        holder.binding.tvUkuran.text =
            when (kavling.kode) {
                "C1" -> "67,12 m2"
                "C6" -> "63,63 m2"
                else -> kavling.ukuran
            }


        // Fill Layout progress settings
        val warna = Color.parseColor(kavling.warna)
        val progress = mapProgressKavling[kavling.kode]?.persentaseBulanIni()

        holder.binding.cardKavling.setCardBackgroundColor(warna)
        holder.binding.fillProgressPersen.setProgressBackgroundColor(warna)
        holder.binding.layoutRoot.setBackgroundColor(warna)
        if (progress != null) {
            Log.d("PROGRESS_PEMBAYARAN", "Progress Kav. ${kavling.kode} is ${progress}%")
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