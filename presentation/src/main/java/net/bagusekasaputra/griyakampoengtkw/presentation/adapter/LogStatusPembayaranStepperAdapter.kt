package net.bagusekasaputra.griyakampoengtkw.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import moe.feng.common.stepperview.IStepperAdapter
import moe.feng.common.stepperview.VerticalStepperItemView
import moe.feng.common.stepperview.VerticalStepperView
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.StatusPembayaranLayoutHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.LayoutStepperLogStatusPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.toSlashedDate


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

        val doneIconTint = StatusPembayaranLayoutHelper.getIndicatorTintColor(status)
        parent?.doneIcon?.setTint(
            StatusPembayaranLayoutHelper.getColor(
                context!!,
                doneIconTint
            )
        )

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

        if (status is StatusPembayaran.Batal) {
            stepperBinding.btnLihatTabel.visibility = View.VISIBLE
            stepperBinding.btnLihatTabel.setOnClickListener {
                // TODO
                Toast.makeText(context!!, "Stub!", Toast.LENGTH_SHORT).show()
            }
        }

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