package net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.LogStatusPembayaranStepperAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.RecyclerItemLogPembayaranStepperBinding

class LogStatusPembayaranRecyclerAdapter(
    private val listLogStatuses: List<List<StatusPembayaran.LogStatus>>,
): RecyclerView.Adapter<LogStatusPembayaranRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(binding: RecyclerItemLogPembayaranStepperBinding): RecyclerView.ViewHolder(binding.root) {
        val container = binding.root
        val stepper = binding.verticalStepperLogStatusPembayaran
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerItemLogPembayaranStepperBinding.inflate(inflater, parent, false)

        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listLogStatuses.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val logStatuses = listLogStatuses[position]

        holder.stepper.apply {
            stepperAdapter = LogStatusPembayaranStepperAdapter(this, logStatuses)
        }
    }
}