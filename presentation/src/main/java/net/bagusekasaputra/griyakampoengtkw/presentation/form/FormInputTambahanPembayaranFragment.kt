package net.bagusekasaputra.griyakampoengtkw.presentation.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToLong
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.InsertTambahanPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.addThousandTextListener
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormInputTambahanPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.FormUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel

class FormInputTambahanPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentFormInputTambahanPembayaranBinding
    private val pembayaranViewModel: FormPembayaranViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.also { bundle ->
            val parcelable: Parcelable? = BundleCompat.getParcelable(
                bundle, FormActivity.EXTRAS_PARCEL, Parcelable::class.java
            )
            when (parcelable) {
                is InsertTambahanPembayaranParcel -> {
                    pembayaranViewModel.currentKavlingKode = parcelable.kavling
                    pembayaranViewModel.formIsEditMode = false
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFormInputTambahanPembayaranBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formActivity = requireActivity() as FormActivity
        with(formActivity.getToolbar()) {
            title = "Tambahan Pembayaran"
            subtitle = "Kavling ${pembayaranViewModel.currentKavlingKode!!}"
        }

        binding.edtJumlahUangDibayar.addThousandTextListener()

        val datePickerDialog = DatePickerHelper(requireContext(), binding.btnPilihTanggal, binding.edtTanggal)
        if (pembayaranViewModel.formIsEditMode) {
            datePickerDialog.setupDateDefaultOrPick(false)
        } else {
            datePickerDialog.setupDateDefaultOrPick(true)
        }

        formActivity.getFabDone().setOnClickListener {
            val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                binding.edtJumlahUangDibayar,
                binding.edtTanggal
            )
            val isEmptyKategori = binding.rgKategori.checkedRadioButtonId == -1

            if (!isInvalidEdt && !isEmptyKategori) {
                val kategori = binding.rgKategori.checkedRadioButtonId.let {
                    if (it == binding.rbTambahanLuasan.id) {
                        TambahanPembayaran.Kategori.LUASAN
                    } else {
                        TambahanPembayaran.Kategori.PEMBANGUNAN
                    }
                }
                val jumlahUang = binding.edtJumlahUangDibayar.text?.toString()?.numericToLong() ?: 0L
                val tanggal = binding.edtTanggal.text?.toString()?.toDate() ?: "01/01/2020".toDate()
                val keterangan = binding.edtKeterangan.text?.toString() ?: ""

                val tambahanPembayaran = TambahanPembayaran(
                    kavling = pembayaranViewModel.currentKavlingKode!!,
                    jumlahUang = jumlahUang,
                    sudahIsiFoto = false,
                    tanggal = tanggal,
                    keterangan = keterangan,
                    kategori = kategori
                )
                pembayaranViewModel.insertTambahanPembayaran(tambahanPembayaran)

                // Prevent from double click
                formActivity.getFabDone().hide()
            } else {
                Toast.makeText(requireContext(), "Form tidak valid!!", Toast.LENGTH_SHORT).show()
            }
        }

        setupViewModel()
    }

    private fun setupViewModel() {
        pembayaranViewModel.insertTambahanPembayaranOperation.observe(requireActivity()) {
            if (it != null) {
                when (it) {
                    is UiState.Loading -> {
                        binding.layoutContent.visibility = View.GONE
                        binding.layoutLoading.visibility = View.VISIBLE

                        Snackbar.make(binding.root, "Memproses tambahan pembayaran ...", Snackbar.LENGTH_INDEFINITE)
                            .show()
                    }
                    is UiState.Failure -> {
                        val failMsgIntent = Intent().apply {
                            putExtra(FormActivity.EXTRAS_FAIL_MSG, it.failMsg)
                        }
                        FormUtil.sendResultAndExit(requireActivity(), Activity.RESULT_CANCELED, failMsgIntent)
                    }
                    is UiState.Success -> {
                        FormUtil.sendResultAndExit(
                            requireActivity(), Activity.RESULT_OK, null
                        )
                    }
                }
            }
        }
    }

}