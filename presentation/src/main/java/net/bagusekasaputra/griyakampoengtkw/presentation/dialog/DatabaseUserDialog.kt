package net.bagusekasaputra.griyakampoengtkw.presentation.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DatabaseUser
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.DialogDatabaseUserBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.util.Consts
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DialogUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.DatabaseUserViewModel

@AndroidEntryPoint
class DatabaseUserDialog(
    private val user: DatabaseUser? = null
): DialogFragment() {

    private val databaseUserViewModel by viewModels<DatabaseUserViewModel>()
    private lateinit var binding: DialogDatabaseUserBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogDatabaseUserBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(binding.root)
            setCancelable(false)
        }.create()

        DialogUtil.additionalDialogSetting(requireContext(), dialog)

        binding.spinnerNegaraBekerja.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            Consts.negaraBekerjaList
        )

        val editMode = user != null
        DatePickerHelper(requireContext(), binding.btnPilihTanggal, binding.edtTanggalEntry)
            .setupDateDefaultOrPick(!editMode)
        // TODO: On Edit mode

        binding.btnTambahkan.setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                binding.edtNama,
                binding.edtTanggalEntry,
                binding.edtLokasiIndo,
            )
            if (!isInvalidEdt) {
                val nama = binding.edtNama.text.toString()
                val tanggal = binding.edtTanggalEntry.text.toString()
                val noHp = binding.edtNoHp.text.toString()
                val tiktok = binding.edtTiktok.text.toString()
                val lokasiIndo = binding.edtLokasiIndo.text.toString()
                val negaraBekerja = Consts.negaraBekerjaList[binding.spinnerNegaraBekerja.selectedItemPosition]
                val keterangan = binding.edtKeterangan.text.toString()

                val user = DatabaseUser(
                    nama = nama,
                    tanggal = tanggal.toDate(),
                    noHp = noHp,
                    _usernameTiktok = tiktok,
                    lokasiIndo = lokasiIndo,
                    negaraBekerja = negaraBekerja,
                    keterangan = keterangan
                )

                databaseUserViewModel.insertNewDatabaseUser(user,
                    onLoading = {
                        binding.btnTambahkan.text = "Menyimpan ..."
                        binding.btnTambahkan.isEnabled = false
                        binding.btnHapus.isEnabled = false
                    },
                    onSuccess = {
                        Toast.makeText(
                            requireContext(),
                            "Berhasil menambahkan user!",
                            Toast.LENGTH_SHORT
                        ).show()

                        dialog.dismiss()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()

                        binding.btnTambahkan.text = if (editMode) "Ubah" else "Tambahkan"
                        binding.btnTambahkan.isEnabled = true
                        binding.btnHapus.isEnabled = true
                    }
                )
            } else {
                Snackbar.make(binding.root, "Terdapat data yang tidak valid!", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnBatal.setOnClickListener {
            databaseUserViewModel.writeJob?.cancel()

            dialog.dismiss()
        }

        binding.btnHapus.setOnClickListener {
            // TODO: On Hapus
            Snackbar.make(binding.root, "Stub!", Snackbar.LENGTH_SHORT).show()
        }

        return dialog
    }
}