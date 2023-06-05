package net.bagusekasaputra.griyakampoengtkw.presentation.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.addThousandTextListener
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormInputPembayaranKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.FormUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel

@AndroidEntryPoint
class FormInputPembayaranKavlingFragment : Fragment() {

    private lateinit var binding: FragmentFormInputPembayaranKavlingBinding
    private val viewModel by activityViewModels<FormPembayaranViewModel>()

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(viewModel) {
            currentKavlingKode = arguments?.getString(FormActivity.EXTRAS_KAVLING)
            currentTermin = arguments?.getString(FormActivity.EXTRAS_TERMIN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFormInputPembayaranKavlingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formActivity = (requireActivity() as FormActivity)
        isEditMode = (!viewModel.currentKavlingKode.isNullOrEmpty()
                    && !viewModel.currentTermin.isNullOrEmpty())

        // Setup toolbar title and subtitle
        with(formActivity.getToolbar()) {
            title = if (isEditMode) "Ubah Pembayaran"
                else "Tambahkan Pembayaran"

            if (isEditMode) {
                subtitle = "${viewModel.currentKavlingKode} - ${viewModel.currentTermin}"
            }
        }


        with(binding) {
            edtJumlahUangDibayar.addThousandTextListener()

            if (isEditMode) {
                viewModel.getSinglePembayaran(
                    kavling = viewModel.currentKavlingKode!!,
                    termin = viewModel.currentTermin!!,
                )

                setupViewModelForEditMode()
            } else {
                // Sync pembayaran, needed to get next sequence termin if not EditMode
                syncPembayaran()
            }

            formActivity.getFabDone().setOnClickListener {
                val isInvalidEdt = InputUtil.isNullOrEmptyEditTexts(
                    edtTermin, edtTanggal, edtJumlahUangDibayar
                )

                if (!isInvalidEdt) {
                    val jenisTermin = rgJenisPembayaran.getSelectedJenisTermin()
                    val urutanTermin = edtTermin.text.toString().toInt()
                    val tanggalPembayaran = edtTanggal.text.toString()
                    val jumlahUangDibayar = edtJumlahUangDibayar.text.toString()
                    val keteranganProgress = edtKeteranganProgress.text.toString().ifBlank { "-" }

                    val pembayaran = Pembayaran(
                        termin = "$jenisTermin $urutanTermin",
                        tanggal = tanggalPembayaran,
                        jumlahUangDibayar = jumlahUangDibayar,
                        keterangan = keteranganProgress,
                        timeMillis = System.currentTimeMillis(),
                    )
                    if (isEditMode) {
                        viewModel.updatePembayaran(
                            kavlingKode = viewModel.currentKavlingKode!!,
                            termin = viewModel.currentTermin!!,
                            newPembayaran = pembayaran,
                        )
                    } else {
                        TODO("Not yet implemented")
                    }
                } else {
                    Toast.makeText(requireContext(), "Input masih belum benar!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun syncPembayaran() {
        TODO()
    }

    private fun setupViewModelForEditMode() {
        with(binding) {
            viewModel.pembayaran.observe(requireActivity()) {
                it?.also { uiState ->
                    Log.d("FORM_INPUT_PEMBAYARAN", "FormInputPembayaranFragment: Params -> Kavling: ${viewModel.currentKavlingKode}, Termin: ${viewModel.currentTermin}")

                    when (uiState) {
                        is UiState.Loading -> {
                            root.alpha = 0.1f
                        }
                        is UiState.Success -> {
                            root.alpha = 1f

                            val pembayaran = uiState.data
                            if (pembayaran != null) {
                                Log.d("FORM_INPUT_PEMBAYARAN", "Got pembayaran $pembayaran !")

                                rgJenisPembayaran.setCheckedJenisTermin(pembayaran.getJenisTermin())
                                disableAllJenisPembayaranRadioButtons()

                                edtTermin.setText(pembayaran.getUrutan().toString())
                                edtTermin.isEnabled = false

                                edtTanggal.setText(pembayaran.tanggal)
                                DatePickerHelper(requireContext(), btnPilihTanggal, edtTanggal)
                                    .setupDateDefaultOrPick(false)

                                edtJumlahUangDibayar.setText(pembayaran.jumlahUangDibayar)

                                edtKeteranganProgress.setText(pembayaran.keterangan)
                            } else {
                                Toast.makeText(requireContext(), "Pembayaran tidak ditemukan!", Toast.LENGTH_LONG).show()
                            }
                        }
                        is UiState.Failure -> {
                            Toast.makeText(requireContext(), uiState.failMsg, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            viewModel.ubahPembayaranOperation.observe(requireActivity()) {
                it?.also { uiState ->
                    val progressSnackbar = Snackbar.make(root, "Memproses perubahan data ..", Snackbar.LENGTH_INDEFINITE)
                    val formActivity = (requireActivity() as FormActivity)
                    val dataToSend = Intent()

                    when (uiState) {
                        is UiState.Loading -> {
                            progressSnackbar.show()
                        }
                        is UiState.Success -> {
                            dataToSend.putExtra(FormActivity.EXTRAS_SUCCESS_DATA, "Berhasil mengubah pembayaran ${viewModel.currentTermin}!")

                            FormUtil.sendResultAndExit(formActivity, Activity.RESULT_OK, dataToSend)
                        }
                        is UiState.Failure -> {
                            dataToSend.putExtra(FormActivity.EXTRAS_FAIL_MSG, uiState.failMsg)

                            FormUtil.sendResultAndExit(formActivity, Activity.RESULT_CANCELED, dataToSend)
                        }
                    }
                }
            }
        }
    }

    private fun RadioGroup.setCheckedJenisTermin(jenisTermin: String) {
        with(binding) {
            val checkedTermin = when (jenisTermin) {
                Pembayaran.JenisPembayaran.ITJ.text -> rbItj.id
                Pembayaran.JenisPembayaran.DP.text -> rbDp.id
                else -> rbTermin.id
            }

            check(checkedTermin)
        }
    }

    private fun disableAllJenisPembayaranRadioButtons() {
        with(binding) {
            rbItj.isEnabled = false
            rbDp.isEnabled = false
            rbTermin.isEnabled = false
        }
    }

    private fun RadioGroup.getSelectedJenisTermin(): String {
        return with(binding) {
            when (checkedRadioButtonId) {
                rbItj.id -> Pembayaran.JenisPembayaran.ITJ.text
                rbDp.id -> Pembayaran.JenisPembayaran.DP.text
                rbTermin.id -> Pembayaran.JenisPembayaran.TERMIN.text
                else -> Pembayaran.JenisPembayaran.TERMIN.text
            }
        }
    }
}