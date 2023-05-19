package net.bagusekasaputra.griyakampoengtkw.presentation.custom

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.adapter.recyclerview.LogStatusPembayaranRecyclerAdapter
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutStatusPembayaranBinding

class StatusPembayaranLayoutHelper(
    private val binding: LayoutStatusPembayaranBinding,
) {

    @SuppressLint("SetTextI18n")
    fun onLoading() {
        setDrawableIndikator(R.drawable.indicator_status_pembayaran, android.R.color.darker_gray)
        binding.tvStatus.text = "Memuat status pembayaran ..."

        binding.root.isClickable = false
//        binding.viewPagerLogStatusPembayaran.visibility = View.GONE
    }

    fun onSuccess(statusPembayaran: StatusPembayaran) {
        val currentStatus = statusPembayaran.currentStatus
        val indikatorTint = getIndicatorTintColor(currentStatus)
        val textStatus = currentStatus.getTitle()
        setDrawableIndikator(R.drawable.indicator_status_pembayaran, indikatorTint)
        binding.tvStatus.text = textStatus


        binding.recyclerStepper.apply {
            val context = binding.root.context
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = LogStatusPembayaranRecyclerAdapter(statusPembayaran.listLogStatuses)
        }
//        binding.viewPagerLogStatusPembayaran.visibility = View.GONE


        binding.root.isCheckable = true
        binding.root.setOnClickListener {
            val recyclerViewVisibility = binding.recyclerStepper.visibility
            binding.recyclerStepper.visibility = if (recyclerViewVisibility == View.VISIBLE)
                View.GONE else View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    fun onFailure() {
        setDrawableIndikator(R.drawable.baseline_warning_24, R.color.abang)
        binding.tvStatus.text = "Gagal memuat!"

        binding.root.isClickable = false
//        binding.viewPagerLogStatusPembayaran.visibility = View.GONE
    }

    private fun setDrawableIndikator(drawableResId: Int, tintColor: Int) {
        val context = binding.root.context
        val drawableIndikator = ContextCompat.getDrawable(context, drawableResId)
        drawableIndikator?.setTint(getColor(context, tintColor))

        binding.imgIndikatorStatus.background = drawableIndikator
    }

    companion object {
        fun getIndicatorTintColor(logStatus: StatusPembayaran.LogStatus): Int {
            return when (logStatus) {
                is StatusPembayaran.Nil -> R.color.status_pembayaran_nil
                is StatusPembayaran.Aktif -> R.color.status_pembayaran_aktif
                is StatusPembayaran.Suspend -> R.color.status_pembayaran_suspend
                is StatusPembayaran.Jeda -> R.color.status_pembayaran_jeda
                is StatusPembayaran.Batal -> R.color.status_pembayaran_batal
            }
        }

        fun getColor(context: Context, resId: Int): Int {
            return ContextCompat.getColor(context, resId)
        }
    }
}