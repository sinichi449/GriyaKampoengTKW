package net.bagusekasaputra.griyakampoengtkw.presentation.form

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.BundleCompat
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.InsertTambahLuasanPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.UpdateTambahLuasanPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.addThousandTextListener
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormInputTambahLuasanPembayaranBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormInputViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel

@AndroidEntryPoint
class FormInputTambahLuasanPembayaranFragment : Fragment() {

    private lateinit var binding: FragmentFormInputTambahLuasanPembayaranBinding
    private val pembayaranViewModel by activityViewModels<FormPembayaranViewModel>()
    private val formViewModel by activityViewModels<FormInputViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize essential data to ViewModel which will be used later
        arguments?.also { bundle ->
            val parcelable: Parcelable? = BundleCompat.getParcelable(
                bundle, FormActivity.EXTRAS_PARCEL, Parcelable::class.java
            )

            when (parcelable) {
                is InsertTambahLuasanPembayaranParcel -> {
                    pembayaranViewModel.currentKavlingKode = parcelable.kavling
                    pembayaranViewModel.formIsEditMode = false
                }
                is UpdateTambahLuasanPembayaranParcel -> {
                    pembayaranViewModel.currentKavlingKode = parcelable.kavling
                    pembayaranViewModel.currentTambahLuasanId = parcelable.id
                    pembayaranViewModel.formIsEditMode = true
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFormInputTambahLuasanPembayaranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formActivity = (requireActivity() as FormActivity)

        // Setup toolbar title and subtitle
        with(formActivity.getToolbar()) {
            title = if (pembayaranViewModel.formIsEditMode) {
                "Ubah Tambah Luasan"
            } else {
                "Tambahkan Tambahan Luasan"
            }
            subtitle = "Kav. ${pembayaranViewModel.currentKavlingKode}"
        }

        with(binding) {
            edtJumlahUangDibayar.addThousandTextListener()

            val datePickerHelper = DatePickerHelper(requireContext(), btnPilihTanggal, edtTanggal)

            if (pembayaranViewModel.formIsEditMode) {
                datePickerHelper.setupDateDefaultOrPick(false)

                // Get specific tambahan luasan id and kavling
                pembayaranViewModel.getTambahanLuasPembayaran(
                    kavling = pembayaranViewModel.currentKavlingKode!!,
                    id = pembayaranViewModel.currentTambahLuasanId!!,
                )

                setupViewModelForEditMode()
            } else {
                datePickerHelper.setupDateDefaultOrPick(true)

                setupViewModel()
            }
        }

        formActivity.getFabDone().setOnClickListener {
            Toast.makeText(requireContext(), "Oke good!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        // TODO
    }

    private fun setupViewModelForEditMode() {
        pembayaranViewModel.tambahanLuasItem.observe(requireActivity()) { k ->
            k?.also { uiState ->
                when (uiState) {
                    is UiState.Loading -> { onLoading(true) }
                    is UiState.Success -> {
                        onLoading(false)

                        with(binding) {
                            edtTanggal.setText(uiState.data?.tanggal)
                            edtJumlahUangDibayar.setText(uiState.data?.jumlahUang?.numericToString())
                            edtKeterangan.setText(uiState.data?.keterangan)
                        }
                    }
                    is UiState.Failure -> {
                        Toast.makeText(requireContext(), uiState.failMsg, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun onLoading(loading: Boolean) {
        with(binding) {
            layoutLoading.visibility = if (loading) View.VISIBLE else View.GONE
            layoutForm.visibility = if (loading) View.GONE else View.VISIBLE
        }
    }

}