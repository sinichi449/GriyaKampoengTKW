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
import com.google.android.material.snackbar.Snackbar
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

        val listOpsiTimeframe = mutableListOf<String>().apply {
            add(BaselinePembayaran.OPSI_TIMEFRAME_BULAN, "Bulan")
            add(BaselinePembayaran.OPSI_TIMEFRAME_TAHUN, "Tahun")
        }
        val isEditMode = baselinePembayaran != null

        with(binding) {
            edtInfoHargaKavling.setText("Rp. ${NumberUtil.formatLongToString(hargaKavling.hargaLong)}")

            spinnerTimeframeAngsuran.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, listOpsiTimeframe
            )

            edtTanggalPembelian.apply {
                val datePickerHelper = DatePickerHelper(requireContext(), binding.btnPilihTanggalPembelian, this)

                if (!tanggalPembelian.isNullOrEmpty()) {
                    setText(tanggalPembelian)

                    datePickerHelper.setupDateDefaultOrPick(false)
                } else {
                    datePickerHelper.setupDateDefaultOrPick(true)
                }
            }

            edtUangAngsuranPerBulan.addTextChangedListener {
                it?.toString()?.also { string ->
                    if (string.isNotEmpty()) {
                        val text = "= Rp. ${NumberUtil.formatDoubleToString(string.toDouble())}"

                        binding.tvInfoParsedUangAngsuranRupiah.text = text
                    } else {
                        binding.tvInfoParsedUangAngsuranRupiah.text = "Rp. 0"
                    }
                }
            }

            if (isEditMode) {
                tvDialogTitle.text = "Ubah Angsuran Bulanan"
                btnTambahkan.text = "Ubah"
                baselinePembayaran?.also {
                    edtOpsiTimeframeAngsuran.setText(it.opsiBulan.toString())
                    spinnerTimeframeAngsuran.setSelection(BaselinePembayaran.OPSI_TIMEFRAME_BULAN) // Default to "Bulan"

                    tvPerhitungan.text = keteranganPerhitunganAngsuran(
                        hargaKavling, BaselinePembayaran.OPSI_TIMEFRAME_BULAN, it.opsiBulan
                    )
                    edtUangAngsuranPerBulan.setText(it.jumlahUang.toString())

                    tvInfoParsedUangAngsuranRupiah.text = "= Rp. ${it.parsedJumlahUang}"
                    edtMaksimalTanggalPembayaran.setText(it.tanggalPembayaranMaks.toString())

                    if (!tanggalPembelian.isNullOrEmpty()) {
                        tvTanggalAngsuranSelesai.text = BaselinePembayaran
                            .hitungTanggalAngsuranSelesai(
                                tanggalPembelian.toDate(),
                                BaselinePembayaran.OPSI_TIMEFRAME_BULAN, it.opsiBulan
                            )
                            .toStringAndLongBulan()
                    } else {
                        tvTanggalAngsuranSelesai.text = "-"
                    }
                }
            } else {
                tvDialogTitle.text = "Tambah Angsuran Bulanan"
                btnTambahkan.text = "Tambahkan"
                tvTanggalAngsuranSelesai.text = "-"
            }

            btnHitung.setOnClickListener {
                val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                    edtOpsiTimeframeAngsuran, edtTanggalPembelian,
                )
                if (!isInvalidEdt) {
                    val timeFrame = edtOpsiTimeframeAngsuran.text.toString().toInt()
                    val opsiTimeFrame = spinnerTimeframeAngsuran.selectedItemPosition
                    val biayaAngsuranPerBulan = try {
                        BaselinePembayaran.hitungAngsuranPerBulan(hargaKavling, opsiTimeFrame, timeFrame)
                    } catch (e: Exception) {
                        e.printStackTrace()

                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()

                        0.0
                    }
                    val tanggalAngsuranSelesai = edtTanggalPembelian.text.toString().toDate().let {
                        BaselinePembayaran.hitungTanggalAngsuranSelesai(it, opsiTimeFrame, timeFrame)
                    }

                    tvPerhitungan.text = keteranganPerhitunganAngsuran(
                        hargaKavling, opsiTimeFrame, timeFrame
                    )

                    edtUangAngsuranPerBulan.setText(biayaAngsuranPerBulan.let {
                        // Prevent 1E77 or alike (exponents)
                        DecimalFormat("#", DecimalFormatSymbols(Locale.US))
                            .format(it)
                    })

                    tvTanggalAngsuranSelesai.text = tanggalAngsuranSelesai.toStringAndLongBulan()
                } else {
                    Toast.makeText(requireContext(), "Input masih belum benar!", Toast.LENGTH_LONG)
                        .show()
                }
            }

            btnTambahkan.setOnClickListener {
                val isInvalidInput = InputUtil.isNullOrEmptyEditTexts(
                    edtOpsiTimeframeAngsuran, edtUangAngsuranPerBulan, edtMaksimalTanggalPembayaran,
                )

                if (!isInvalidInput) {
                    val opsiBulan = spinnerTimeframeAngsuran.selectedItemPosition.let { position ->
                        val timeFrame = edtOpsiTimeframeAngsuran.text.toString().toInt()

                        when (position) {
                            BaselinePembayaran.OPSI_TIMEFRAME_BULAN -> timeFrame
                            BaselinePembayaran.OPSI_TIMEFRAME_TAHUN -> timeFrame * 12
                            else -> 0
                        }
                    }
                    val biayaAngsuran = edtUangAngsuranPerBulan.text?.toString()?.toLong() ?: 0L
                    val tanggalPembayaranMaksimal = edtMaksimalTanggalPembayaran.text.toString().toInt()
                    val baselinePembayaran = BaselinePembayaran(
                        currentKavlingKode,
                        opsiBulan,
                        biayaAngsuran,
                        tanggalPembayaranMaksimal
                    )

                    pembayaranViewModel.insertBaselinePembayaran(
                        baselinePembayaran = baselinePembayaran,
                        onLoading = {
                            btnTambahkan.text = "Menyimpan ..."
                            btnTambahkan.isEnabled = false
                            btnBatal.isEnabled = false
                        },
                        onComplete = {
                            dialog.dismiss()

                            Snackbar.make(root, "Berhasil mengubah Angsuran Bulanan", Snackbar.LENGTH_SHORT)
                                .show()
                        },
                        onFailure = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()

                            btnTambahkan.isEnabled = true
                            btnTambahkan.text = if (isEditMode) "Ubah" else "Tambahkan"
                            btnBatal.isEnabled = true
                        }
                    )
                }
            }

            btnBatal.setOnClickListener {
                dialog.dismiss()
            }
        }

        return dialog
    }

    private fun keteranganPerhitunganAngsuran(
        hargaKavling: HargaKavling,
        opsiTimeFrame: Int,
        timeFrame: Int,
    ): String {
        val parsedHargaKavling = NumberUtil.formatLongToString(hargaKavling.hargaLong)
        val parsedUangAngsuran = BaselinePembayaran.hitungAngsuranPerBulan(
            hargaKavling, opsiTimeFrame, timeFrame
        ).let {
            NumberUtil.formatDoubleToString(it)
        }
        val parsedOpsiTimeFrame = when (opsiTimeFrame) {
            BaselinePembayaran.OPSI_TIMEFRAME_BULAN -> "Bulan"
            BaselinePembayaran.OPSI_TIMEFRAME_TAHUN -> "Tahun"
            else -> "NULL"
        }

        return "$parsedHargaKavling / $timeFrame $parsedOpsiTimeFrame = Rp. $parsedUangAngsuran"
    }
}