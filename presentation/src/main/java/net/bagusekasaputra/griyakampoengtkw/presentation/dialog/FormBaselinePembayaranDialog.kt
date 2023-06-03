package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toStringAndLongBulan
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogBaselineAngsuranPerBulanBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@AndroidEntryPoint
class FormBaselinePembayaranDialog(
    private val currentKavlingKode: String,
    private val hargaKavling: HargaKavling,
    private val baselinePembayaran: BaselinePembayaran? = null,
    private val tanggalPembelian: String? = null,
): DialogFragment() {

    private lateinit var binding: DialogBaselineAngsuranPerBulanBinding
    private val pembayaranViewModel: FormPembayaranViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogBaselineAngsuranPerBulanBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setCancelable(false)
            setView(binding.root)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), dialog)

        binding.edtInfoHargaKavling.setText("Rp. ${NumberUtil.formatLongToString(hargaKavling.hargaLong)}")
        binding.spinnerTimeframeAngsuran.apply {
            val listOpsiTimeframe = mutableListOf<String>().apply {
                add(BaselinePembayaran.OPSI_TIMEFRAME_BULAN, "Bulan")
                add(BaselinePembayaran.OPSI_TIMEFRAME_TAHUN, "Tahun")
            }
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                listOpsiTimeframe
            )
        }

        val isEditMode = baselinePembayaran != null
        if (isEditMode) {
            binding.tvDialogTitle.text = "Ubah Angsuran Bulanan"
            binding.btnTambahkan.text = "Ubah"
            baselinePembayaran?.also {
                binding.edtOpsiTimeframeAngsuran.setText(it.opsiBulan.toString())
                binding.spinnerTimeframeAngsuran.setSelection(BaselinePembayaran.OPSI_TIMEFRAME_BULAN) // Default to "Bulan"
                binding.edtUangAngsuranPerBulan.setText(it.jumlahUang.toString())

                binding.tvPerhitungan.text = StringBuilder().apply {
                    val parsedHargaKavling = NumberUtil.formatLongToString(hargaKavling.hargaLong)
                    val uangAngsuran = BaselinePembayaran.hitungAngsuranPerBulan(
                        hargaKavling, BaselinePembayaran.OPSI_TIMEFRAME_BULAN,
                        it.opsiBulan
                    ).let { parsedUangAngsuran ->
                        NumberUtil.formatDoubleToString(parsedUangAngsuran)
                    }

                    append("$parsedHargaKavling ")
                    append("/ ${it.opsiBulan} Bulan ")
                    append("= Rp. $uangAngsuran")
                }.toString()

                binding.tvInfoParsedUangAngsuranRupiah.text = "= Rp. ${it.parsedJumlahUang}"
                binding.edtMaksimalTanggalPembayaran.setText(it.tanggalPembayaranMaks.toString())

                if (!tanggalPembelian.isNullOrEmpty()) {
                    binding.tvTanggalAngsuranSelesai.text = BaselinePembayaran
                        .hitungTanggalAngsuranSelesai(
                            tanggalPembelian.toDate(),
                            BaselinePembayaran.OPSI_TIMEFRAME_BULAN, it.opsiBulan
                        )
                        .toStringAndLongBulan()
                } else {
                    binding.tvTanggalAngsuranSelesai.text = "-"
                }
            }
        } else {
            binding.tvDialogTitle.text = "Tambah Angsuran Bulanan"
            binding.btnTambahkan.text = "Tambahkan"
            binding.tvTanggalAngsuranSelesai.text = "-"
        }

        binding.edtTanggalPembelian.apply {
            if (!tanggalPembelian.isNullOrEmpty()) {
                setText(tanggalPembelian)
            } else {
                DatePickerHelper(requireContext(), binding.btnPilihTanggalPembelian, this)
            }
        }

        binding.btnHitung.setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                binding.edtOpsiTimeframeAngsuran,
                binding.edtTanggalPembelian,
            )
            if (!isInvalidEdt) {
                val timeFrame = binding.edtOpsiTimeframeAngsuran.text.toString().toInt()
                val opsiTimeFrame = binding.spinnerTimeframeAngsuran.selectedItemPosition
                val biayaAngsuranPerBulan = try {
                    BaselinePembayaran.hitungAngsuranPerBulan(hargaKavling, opsiTimeFrame, timeFrame)
                } catch (e: Exception) {
                    e.printStackTrace()

                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()

                    0.0
                }
                val tanggalAngsuranSelesai = binding.edtTanggalPembelian.text.toString().toDate().let {
                    BaselinePembayaran.hitungTanggalAngsuranSelesai(it, opsiTimeFrame, timeFrame)
                }

                binding.tvPerhitungan.text = StringBuilder().run {
                    val hargaKavling = NumberUtil.formatLongToString(hargaKavling.hargaLong)
                    val timeFrameAngsuran = when (opsiTimeFrame) {
                        BaselinePembayaran.OPSI_TIMEFRAME_TAHUN -> "$timeFrame Tahun"
                        BaselinePembayaran.OPSI_TIMEFRAME_BULAN -> "$timeFrame Bulan"
                        else -> "NULL"
                    }

                    append("$hargaKavling ")
                    append("/ $timeFrameAngsuran ")
                    append("= Rp. ${NumberUtil.formatDoubleToString(biayaAngsuranPerBulan)}")

                    toString()
                }

                binding.edtUangAngsuranPerBulan.setText(biayaAngsuranPerBulan.let {
                    DecimalFormat("#", DecimalFormatSymbols(Locale.US))
                        .format(it)
                    // Prevent 1E77 or alike (exponents)
                })

                binding.tvTanggalAngsuranSelesai.text = tanggalAngsuranSelesai.toStringAndLongBulan()
            } else {
                Toast.makeText(requireContext(), "Input masih belum benar!", Toast.LENGTH_LONG)
                    .show()
            }
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
                        binding.btnTambahkan.text = if (isEditMode) "Ubah" else "Tambahkan"
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