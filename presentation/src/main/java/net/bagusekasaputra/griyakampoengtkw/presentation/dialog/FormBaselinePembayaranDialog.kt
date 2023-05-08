package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.R
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogBaselineAngsuranPerBulanBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class FormBaselinePembayaranDialog(
    private val currentKavlingKode: String,
    private val hargaKavling: HargaKavling,
): DialogFragment() {

    private lateinit var binding: DialogBaselineAngsuranPerBulanBinding
    private val pembayaranViewModel: FormPembayaranViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogBaselineAngsuranPerBulanBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setCancelable(false)
            setView(binding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), dialog)

        binding.edtInfoHargaKavling.setText("Rp. ${NumberUtil.formatLongToString(hargaKavling.hargaLong)}")
        binding.spinnerTimeframeAngsuran.apply {
            val listOpsiTimeframe = listOf("Tahun", "Bulan")
            adapter = ArrayAdapter(
                requireContext(), R.layout.simple_spinner_dropdown_item, listOpsiTimeframe
            )
        }
        binding.btnHitung.setOnClickListener {
            val timeFrame = binding.edtOpsiTimeframeAngsuran.text.toString().toInt()
            val opsiTimeFrame = binding.spinnerTimeframeAngsuran.selectedItem.toString()
            val biayaAngsuranPerBulan = try {
                pembayaranViewModel.hitungAngsuranPerBulan(
                    hargaKavling,
                    timeFrame,
                    opsiTimeFrame
                )
            } catch (e: Exception) {
                e.printStackTrace()

                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()

                0.0
            }

            binding.tvPerhitungan.text = StringBuilder().run {
                append("${NumberUtil.formatLongToString(hargaKavling.hargaLong)} ")
                append("/ ${if (opsiTimeFrame == "Tahun") "$timeFrame Tahun (${timeFrame * 12} Bulan) " else "$timeFrame Bulan"} ")
                append("= Rp. ${NumberUtil.formatDoubleToString(biayaAngsuranPerBulan)}")

                toString()
            }

            binding.edtUangAngsuranPerBulan.setText(biayaAngsuranPerBulan.let {
                DecimalFormat("#", DecimalFormatSymbols(Locale.US))
                    .format(it)
                // Prevent 1E77 or alike (exponents)
            })
        }
        binding.edtUangAngsuranPerBulan.addTextChangedListener {
            it?.toString()?.also { string ->
                if (string.isNotEmpty()) {
                    val text = "= Rp. ${NumberUtil.formatDoubleToString(string.toDouble())}"

                    binding.tvInfoParsedUangAngsuranRupiah.text = text
                } else {
                    binding.tvInfoParsedUangAngsuranRupiah.text = "Rp. 0"
                }
            }
        }

        binding.btnTambahkan.setOnClickListener {
            val isInvalidInput = InputUtil.isNullOrEmptyEditTexts(
                binding.edtOpsiTimeframeAngsuran,
                binding.edtUangAngsuranPerBulan,
                binding.edtMaksimalTanggalPembayaran,
            )

            if (!isInvalidInput) {
                val opsiBulan = binding.spinnerTimeframeAngsuran.selectedItemPosition.let { position ->
                    val numTimeFrame = binding.edtOpsiTimeframeAngsuran.text.toString().toInt()
                    if (position == 0) {
                        numTimeFrame * 12
                    } else {
                        numTimeFrame
                    }
                }
                val biayaAngsuran = binding.edtUangAngsuranPerBulan.text?.toString()?.toLong() ?: 0L
                val tanggalPembayaranMaksimal = binding.edtMaksimalTanggalPembayaran.text.toString().toInt()
                val baselinePembayaran = BaselinePembayaran(currentKavlingKode, opsiBulan, biayaAngsuran, tanggalPembayaranMaksimal)

                pembayaranViewModel.insertBaselinePembayaran(
                    baselinePembayaran = baselinePembayaran,
                    onLoading = {
                        binding.btnTambahkan.text = "Menyimpan ..."
                        binding.btnTambahkan.isEnabled = false
                    },
                    onComplete = {
                        Toast.makeText(requireContext(), "Berhasil mengubah Angsuran Bulanan: Rp ${NumberUtil.formatLongToString(biayaAngsuran)}", Toast.LENGTH_SHORT)
                            .show()

                        dialog.dismiss()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()

                        binding.btnTambahkan.isEnabled = true
                        binding.btnTambahkan.text = "Tambahkan"
                    }
                )
            }
        }
        binding.btnBatal.setOnClickListener {
            // TODO: Cancel tambahkanJob
            dialog.dismiss()
        }

        return dialog
    }
}