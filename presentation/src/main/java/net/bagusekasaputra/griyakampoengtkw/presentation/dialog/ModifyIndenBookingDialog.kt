package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.atwa.filepicker.core.FilePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.ThousandSeparatorTextWatcher
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogModifyIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel

@AndroidEntryPoint
class ModifyIndenBookingDialog: DialogFragment() {

    private val indenBookingViewModel: IndenBookingViewModel by activityViewModels()

    // Must be instantiated here...
    private val filePicker = FilePicker.getInstance(this)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogModifyIndenBookingBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(binding.root)
            setCancelable(false)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), dialog)

        val datePickerHelper = DatePickerHelper(requireContext(), binding.btnPililhTanggal, binding.edtTanggalPembayaran)
        datePickerHelper.setupDateDefaultOrPick(true)

        binding.edtJumlahUangDibayar.apply {
            addTextChangedListener(ThousandSeparatorTextWatcher(this))
        }

        binding.btnPilihFotoPembayaran.setOnClickListener {
            filePicker.pickFile {  meta ->
                val file = meta?.file

                if (file != null) {
                    // TODO
                    binding.edtFotoPembayaranPath.setText(file.absolutePath)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "File Foto Pembayaran tidak ditemukan!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.btnTambahkan.setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                binding.edtNamaCostumer,
                binding.edtTanggalPembayaran,
                binding.edtJumlahUangDibayar,
                binding.edtFotoPembayaranPath,
                // No Hp not included
                // Keterangan not included
            )

            if (!isInvalidEdt) {
                val namaCostumer = binding.edtNamaCostumer.text.toString()
                val tanggalDibayar = binding.edtTanggalPembayaran.text.toString().toDate()
                val jumlahUang = binding.edtJumlahUangDibayar.text.toString().let {
                    NumberUtil.formatStringToLong(it)
                }
                val noHp = binding.edtNoHp.text.toString().ifEmpty { "" }
                val keterangan = binding.edtKeterangan.text.toString().ifEmpty { "-" }

                indenBookingViewModel.insertIndenBooking(
                    indenBooking = IndenBooking(namaCostumer, tanggalDibayar, jumlahUang, noHp, keterangan),
                    onProgress = {
                        binding.btnTambahkan.apply {
                            isEnabled = false
                            text = "Menyimpan ..."
                        }
                    },
                    onComplete = {
                        Toast.makeText(requireContext(), "Berhasil menambahkan Inden Booking!", Toast.LENGTH_SHORT).show()

                        dismiss()
                    },
                    onFailure = {
                        binding.btnTambahkan.apply {
                            isEnabled = true
                            text = "Tambahkan"
                        }
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                )
            }
        }

        binding.btnBatal.setOnClickListener {
            indenBookingViewModel.writeIndenBookingJob?.cancel()

            dismiss()
        }

        return dialog
    }
}