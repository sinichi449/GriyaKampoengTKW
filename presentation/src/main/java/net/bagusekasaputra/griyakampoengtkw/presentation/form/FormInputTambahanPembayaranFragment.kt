package net.bagusekasaputra.griyakampoengtkw.presentation.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.InsertTambahanPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.addThousandTextListener
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormInputTambahanPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.FormUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel
import java.util.Calendar

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
            val tambahanPembayaran = TambahanPembayaran(
                kavling = pembayaranViewModel.currentKavlingKode!!,
                kategori = TambahanPembayaran.Kategori.PEMBANGUNAN,
                jumlahUang = 1_000_000L,
                tanggal = Calendar.getInstance().time,
                keterangan = "",
                sudahIsiFoto = false,
            )

            pembayaranViewModel.insertTambahanPembayaran(tambahanPembayaran)
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