package net.bagusekasaputra.griyakampoengtkw.presentation.custom

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutStatusPembayaranBinding

class StatusPembayaranLayoutHelper(
    private val binding: LayoutStatusPembayaranBinding
) {

    @SuppressLint("SetTextI18n")
    fun onLoading() {
        setDrawableIndikator(R.drawable.indicator_status_pembayaran, android.R.color.darker_gray)
        binding.tvStatus.text = "Memuat status pembayaran ..."
    }

    @Suppress("DEPRECATION")
    fun onSuccess(statusPembayaran: StatusPembayaran) {
        // TODO: show all list
        val currentStatus = statusPembayaran.currentStatus

        val indikatorTint = getIndicatorTintColor(currentStatus)
        val textStatus = getStringStatus(currentStatus)

        setDrawableIndikator(R.drawable.indicator_status_pembayaran, indikatorTint)
        binding.tvStatus.text = textStatus
    }

    fun onFailure() {
        setDrawableIndikator(R.drawable.baseline_warning_24, R.color.abang)
    }

    private fun setDrawableIndikator(drawableResId: Int, tintColor: Int) {
        val drawableIndikator = ContextCompat.getDrawable(binding.root.context, drawableResId)
        drawableIndikator?.setTint(getColor(tintColor))

        binding.imgIndikatorStatus.background = drawableIndikator
    }

    private fun getIndicatorTintColor(status: StatusPembayaran.Status): Int {
        return when (status) {
            is StatusPembayaran.Nil -> R.color.status_pembayaran_nil
            is StatusPembayaran.Aktif -> R.color.status_pembayaran_aktif
            is StatusPembayaran.Suspend -> R.color.status_pembayaran_suspend
            is StatusPembayaran.Jeda -> R.color.status_pembayaran_jeda
            is StatusPembayaran.Batal -> R.color.status_pembayaran_batal
        }
    }

    private fun getStringStatus(status: StatusPembayaran.Status): String {
        return when (status) {
            is StatusPembayaran.Nil -> "NIL"
            is StatusPembayaran.Aktif -> "Aktif"
            is StatusPembayaran.Suspend -> "Suspend"
            is StatusPembayaran.Jeda -> "Jeda"
            is StatusPembayaran.Batal -> "Batal"
        }
    }

    private fun getColor(resId: Int): Int {
        return ContextCompat.getColor(binding.root.context, resId)
    }
}