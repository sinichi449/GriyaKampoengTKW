package net.bagusekasaputra.griyakampoengtkw.presentation.custom

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import moe.feng.common.stepperview.IStepperAdapter
import moe.feng.common.stepperview.VerticalStepperItemView
import moe.feng.common.stepperview.VerticalStepperView
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutStatusPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutStepperLogStatusPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.toSlashedDate

class StatusPembayaranLayoutHelper(
    private val binding: LayoutStatusPembayaranBinding
) {

    @SuppressLint("SetTextI18n")
    fun onLoading() {
        setDrawableIndikator(R.drawable.indicator_status_pembayaran, android.R.color.darker_gray)
        binding.tvStatus.text = "Memuat status pembayaran ..."

        binding.root.isClickable = false
        binding.layoutStepper.visibility = View.GONE
    }

    fun onSuccess(statusPembayaran: StatusPembayaran) {
        val currentStatus = statusPembayaran.currentStatus

        val indikatorTint = getIndicatorTintColor(currentStatus)
        val textStatus = currentStatus.getTitle()

        setDrawableIndikator(R.drawable.indicator_status_pembayaran, indikatorTint)
        binding.tvStatus.text = textStatus

        binding.layoutStepper.visibility = View.GONE
        // TODO: Multiple LogStatuses
        binding.verticalStepperLogStatusPembayaran.stepperAdapter = LogStatusPembayaranStepperAdapter(
            binding.verticalStepperLogStatusPembayaran,
            statusPembayaran.listLogStatuses.last()
        )

        binding.root.isCheckable = true
        
        binding.root.setOnClickListener {
            val layoutStepperVisibility = binding.layoutStepper.visibility
            binding.layoutStepper.visibility = if (layoutStepperVisibility == View.VISIBLE)
                View.GONE else View.VISIBLE
        }
    }

    fun onFailure() {
        setDrawableIndikator(R.drawable.baseline_warning_24, R.color.abang)

        binding.root.isClickable = false
        binding.layoutStepper.visibility = View.GONE
    }

    private fun setDrawableIndikator(drawableResId: Int, tintColor: Int) {
        val context = binding.root.context
        val drawableIndikator = ContextCompat.getDrawable(context, drawableResId)
        drawableIndikator?.setTint(getColor(context, tintColor))

        binding.imgIndikatorStatus.background = drawableIndikator
    }

    companion object {
        private fun getIndicatorTintColor(logStatus: StatusPembayaran.LogStatus): Int {
            return when (logStatus) {
                is StatusPembayaran.Nil -> R.color.status_pembayaran_nil
                is StatusPembayaran.Aktif -> R.color.status_pembayaran_aktif
                is StatusPembayaran.Suspend -> R.color.status_pembayaran_suspend
                is StatusPembayaran.Jeda -> R.color.status_pembayaran_jeda
                is StatusPembayaran.Batal -> R.color.status_pembayaran_batal
            }
        }

        private fun getColor(context: Context, resId: Int): Int {
            return ContextCompat.getColor(context, resId)
        }
    }


    class LogStatusPembayaranStepperAdapter(
        private val stepperView: VerticalStepperView,
        private val logStatuses: List<StatusPembayaran.LogStatus>,
    ): IStepperAdapter {
        override fun getTitle(position: Int): CharSequence {
            return logStatuses[position].getTitle()
        }

        override fun getSummary(position: Int): CharSequence {
            return logStatuses[position].tanggal.toSlashedDate()
        }

        override fun size(): Int {
            return logStatuses.size
        }

        @SuppressLint("SetTextI18n")
        override fun onCreateCustomView(position: Int, context: Context?, parent: VerticalStepperItemView?): View {
            val layoutInflater = LayoutInflater.from(context)
            val stepperBinding = LayoutStepperLogStatusPembayaranBinding.inflate(layoutInflater, parent, false)

            val status = logStatuses[position]

            val doneIconTint = getIndicatorTintColor(status)
            parent?.doneIcon?.setTint(getColor(context!!, doneIconTint))

            stepperBinding.itemContent.text = StringBuilder().apply {
                if (status.keterangan.isNotEmpty()) {
                    append(status.keterangan)
                    append("\n\n")
                }
                if (status is StatusPembayaran.Batal) {
                    append("Berikut Tabel Pengembalian uang:")
                    append("\n\n")
                    append("INI TABEL!")
                    append("\n\n")
                }
                append("Perubahan terakhir pada ${status.tanggal.toSlashedDate()}")
            }.toString()

            stepperBinding.buttonNext.setOnClickListener {
                stepperView.nextStep()
            }
            stepperBinding.buttonPrev.setOnClickListener {
                if (position != 0) {
                    stepperView.prevStep()
                } else {
                    stepperView.isAnimationEnabled = !stepperView.isAnimationEnabled
                }
            }

            return stepperBinding.root
        }

        override fun onShow(p0: Int) {

        }

        override fun onHide(p0: Int) {

        }

    }
}