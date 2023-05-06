package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.app.Activity
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.ThousandSeparatorTextWatcher
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogModifyIndenBookingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import net.bagusekasaputra.griyakampoengtkw.presentation.toSlashedDate
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.IndenBookingViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ModifyIndenBookingDialog(
    private val selectedIndenBooking: IndenBooking? = null
): DialogFragment() {

    private val viewModel: IndenBookingViewModel by activityViewModels()

    private lateinit var binding: DialogModifyIndenBookingBinding

    private val registerFotoIndenBookingResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val resultCode = result.resultCode
        val intent = result.data

        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri = intent?.data

                uri?.also {
                    val path = it.toFile().absolutePath
                    viewModel.updatePathFotoIndenBooking(path)
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), ImagePicker.getError(intent), Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(requireContext(), "Operasi dibatalkan", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogModifyIndenBookingBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(binding.root)
            setCancelable(false)
        }.create()

        setupViewModel()

        DialogUtil.additionalDialogSetting(requireContext(), dialog)

        val datePickerHelper = DatePickerHelper(requireContext(), binding.btnPililhTanggal, binding.edtTanggalPembayaran)
        val isEditMode = selectedIndenBooking != null
        if (isEditMode) {
            datePickerHelper.setupDateDefaultOrPick(false)

            binding.tvDialogTitle.text = "Ubah Inden Booking"
            binding.btnTambahkan.text = "Ubah"
            binding.btnHapus.visibility = View.VISIBLE

            // Fill the layout with existing data
            selectedIndenBooking?.also {
                binding.edtNamaCostumer.setText(it.namaCostumer)
                binding.edtTanggalPembayaran.setText(it.tanggalDibayar.toSlashedDate())
                binding.edtJumlahUangDibayar.setText(NumberUtil.formatLongToString(it.jumlahUang))
                binding.edtFotoPembayaranPath.setText(it.fotoPembayaranPath)
                binding.edtNoHp.setText(it.noHp)
                binding.edtKeterangan.setText(it.keterangan)
            }
        } else {
            datePickerHelper.setupDateDefaultOrPick(true)

            binding.tvDialogTitle.text = "Tambahkan Inden Booking"
            binding.btnTambahkan.text = "Tambahkan"
            binding.btnHapus.visibility = View.GONE
        }

        binding.edtJumlahUangDibayar.apply {
            addTextChangedListener(ThousandSeparatorTextWatcher(this))
        }

        binding.btnPilihFotoPembayaran.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(sharedPrefs.getInt("max_size_foto_inden_booking", 512))
                .createIntent {
                    registerFotoIndenBookingResult.launch(it)
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
                val fotoPembayaranPath = binding.edtFotoPembayaranPath.text.toString()
                val noHp = binding.edtNoHp.text.toString().ifEmpty { "" }
                val keterangan = binding.edtKeterangan.text.toString().ifEmpty { "-" }
                val indenBooking = IndenBooking(namaCostumer, tanggalDibayar, jumlahUang, fotoPembayaranPath, noHp, keterangan)

                if (isEditMode) {
                    viewModel.editIndenBooking(
                        oldData = selectedIndenBooking!!,
                        newData = indenBooking,
                        onProgress = {
                            binding.btnTambahkan.isEnabled = false
                            binding.btnHapus.isEnabled = false
                            binding.btnTambahkan.text = "Mengubah ..."
                        },
                        onComplete = {
                            Toast.makeText(requireContext(), "Berhasil mengubah Inden Booking!", Toast.LENGTH_SHORT).show()

                            dismiss()
                        },
                        onFailure = {
                            binding.btnTambahkan.isEnabled = true
                            binding.btnHapus.isEnabled = true
                            binding.btnTambahkan.text = "Ubah"

                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    viewModel.insertIndenBooking(
                        indenBooking = indenBooking,
                        onProgress = {
                            binding.btnTambahkan.apply {
                                isEnabled = false
                                text = "Menyimpan ..."
                            }
                        },
                        onComplete = {
                            Toast.makeText(
                                requireContext(),
                                "Berhasil menambahkan Inden Booking!",
                                Toast.LENGTH_SHORT
                            ).show()

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
        }

        binding.btnBatal.setOnClickListener {
            viewModel.writeIndenBookingJob?.cancel()

            dismiss()
        }

        binding.btnHapus.setOnClickListener {
            deleteConfirmationDialog {
                viewModel.deleteIndenBooking(
                    indenBooking = selectedIndenBooking!!,
                    onProgress = {
                        binding.btnTambahkan.isEnabled = false
                        binding.btnHapus.isEnabled = false
                        binding.btnHapus.text = "Menghapus ..."
                    },
                    onComplete = {
                        Toast.makeText(requireContext(), "Berhasil menghapus!", Toast.LENGTH_SHORT).show()

                        dismiss()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()

                        binding.btnTambahkan.isEnabled = true
                        binding.btnHapus.isEnabled = true
                        binding.btnHapus.text = "Hapus"
                    }
                )
            }
        }

        return dialog
    }

    private fun setupViewModel() {
        viewModel.pathFotoIndenBookingLive.observe(requireActivity()) {
            it?.also { pathFoto ->
                binding.edtFotoPembayaranPath.setText(pathFoto)
            }
        }
    }

    private fun deleteConfirmationDialog(onConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Hapus Inden Booking?")
            setMessage("Apakah Anda yakin untuk menghapus?")
            setPositiveButton("Ya") { dialog, _ ->
                onConfirm()

                dialog.dismiss()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
        }.create()
            .show()
    }
}