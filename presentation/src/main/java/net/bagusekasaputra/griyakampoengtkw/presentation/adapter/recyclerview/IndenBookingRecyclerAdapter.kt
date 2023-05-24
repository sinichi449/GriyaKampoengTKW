package net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutRecyclerIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.IndenBookingUiModel

class IndenBookingRecyclerAdapter(
    private val uiModelIndenBookings: List<IndenBookingUiModel>,
    private val onClick: (position: Int) -> Unit,
): RecyclerView.Adapter<IndenBookingRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(binding: LayoutRecyclerIndenBookingBinding)
        : RecyclerView.ViewHolder(binding.root) {
        val container = binding.root
        val imgFotoIndentitas = binding.imgFotoIdentitas
        val tvNamaCostumer = binding.tvNamaCostumer
        val tvNoIdentitas = binding.tvNoIdentitas
        val tvUangMasuk = binding.tvUangMasuk
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutRecyclerIndenBookingBinding.inflate(inflater, parent, false)

        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return uiModelIndenBookings.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val indenBooking = uiModelIndenBookings[position]

        if (indenBooking.fotoIdentitas != null) {
            holder.imgFotoIndentitas.setImageURI(indenBooking.fotoIdentitas)
        }
        holder.tvNamaCostumer.text = indenBooking.namaCostumer
        holder.tvNoIdentitas.text = indenBooking.noIdentitas
        holder.tvUangMasuk.text = StringBuilder()
            .append("Rp. ")
            .append(NumberUtil.formatLongToString(indenBooking.totalUangMasuk))
            .toString()

        holder.container.setOnClickListener {
            onClick(position)
        }
    }
}