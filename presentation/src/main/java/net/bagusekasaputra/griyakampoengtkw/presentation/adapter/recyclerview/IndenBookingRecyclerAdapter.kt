package net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutRecyclerIndenBookingBinding

class IndenBookingRecyclerAdapter(
    private val indenBookingFragment: Fragment, // Glide need this
    private val indenBookingList: List<IndenBooking>,
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
        return indenBookingList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val indenBooking = indenBookingList[position]

        if (indenBooking.fotoIdentitas != null) {
            holder.imgFotoIndentitas.setImageURI(indenBooking.fotoIdentitas)
        } else {
            val dummyFotoDrawable = ContextCompat.getDrawable(
                indenBookingFragment.requireContext(), R.drawable.avatar_1
            )

            holder.imgFotoIndentitas.setImageDrawable(dummyFotoDrawable)
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