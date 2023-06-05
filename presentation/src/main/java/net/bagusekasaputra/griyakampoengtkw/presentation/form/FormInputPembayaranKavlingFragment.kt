package net.bagusekasaputra.griyakampoengtkw.presentation.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.FormActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.InsertFormPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.activity.UpdateFormPembayaranParcel
import net.bagusekasaputra.griyakampoengtkw.presentation.custom.addThousandTextListener
import net.bagusekasaputra.griyakampoengtkw.presentation.databinding.FragmentFormInputPembayaranKavlingBinding
import net.bagusekasaputra.griyakampoengtkw.presentation.model.UiState
import net.bagusekasaputra.griyakampoengtkw.presentation.util.DatePickerHelper
import net.bagusekasaputra.griyakampoengtkw.presentation.util.FormUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.util.InputUtil
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormInputViewModel
import net.bagusekasaputra.griyakampoengtkw.presentation.viewmodel.FormPembayaranViewModel

@AndroidEntryPoint
class FormInputPembayaranKavlingFragment : Fragment() {

    private lateinit var binding: FragmentFormInputPembayaranKavlingBinding
    private val pembayaranViewModel by activityViewModels<FormPembayaranViewModel>()
    private val formViewModel by activityViewModels<FormInputViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.also { bundle ->
            val parcelable: Parcelable? = BundleCompat.getParcelable(
                bundle, FormActivity.EXTRAS_PARCEL, Parcelable::class.java
            )
            when (parcelable) {
                is InsertFormPembayaranParcel -> {
                    pembayaranViewModel.currentKavlingKode = parcelable.kavling
                    pembayaranViewModel.currentTermin = null

                    pembayaranViewModel.formIsEditMode = false
                }
                is UpdateFormPembayaranParcel -> {
                    pembayaranViewModel.currentKavlingKode = parcelable.kavling
                    pembayaranViewModel.currentTermin = parcelable.termin

                    pembayaranViewModel.formIsEditMode = true
                }
            }
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

        // Setup toolbar title and subtitle
        with(formActivity.getToolbar()) {
            if (pembayaranViewModel.formIsEditMode) {
                title = "Ubah Pembayaran"
                subtitle = "Kav. ${pembayaranViewModel.currentKavlingKode} - ${pembayaranViewModel.currentTermin}"
            } else {
                title = "Tambahkan Pembayaran"
                subtitle = "Kav. ${pembayaranViewModel.currentKavlingKode}"
            }
        }


        with(binding) {
            edtJumlahUangDibayar.addThousandTextListener()

            val datePickerHelper = DatePickerHelper(requireContext(), btnPilihTanggal, edtTanggal)

            if (pembayaranViewModel.formIsEditMode) {
                datePickerHelper.setupDateDefaultOrPick(false)

                pembayaranViewModel.getSinglePembayaran(
                    kavling = pembayaranViewModel.currentKavlingKode!!,
                    termin = pembayaranViewModel.currentTermin!!,
                )

                setupViewModelForEditMode()
            } else {
                datePickerHelper.setupDateDefaultOrPick(true)

                // Sync pembayaran, needed to get next sequence termin if not EditMode
                pembayaranViewModel.fetchPembayaranData(pembayaranViewModel.currentKavlingKode!!)

                setupViewModel()
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
                    if (pembayaranViewModel.formIsEditMode) {
                        val oldPembayaran = pembayaranViewModel.pembayaran.value.run {
                            (this as UiState.Success).data
                        }!!

                        pembayaranViewModel.updatePembayaran(
                            kavlingKode = pembayaranViewModel.currentKavlingKode!!,
                            oldPembayaran = oldPembayaran,
                            newPembayaran = pembayaran,
                        )
                    } else {
                        pembayaranViewModel.addPembayaran(
                            kavlingKode = pembayaranViewModel.currentKavlingKode!!,
                            pembayaran = pembayaran
                        )
                    }

                    disableForms(formActivity.getFabDone())
                } else {
                    Toast.makeText(requireContext(), "Input masih belum benar!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupViewModel() {
        with(binding) {
            pembayaranViewModel.pembayaranList.observe(requireActivity()) {
                it?.also { uiState ->
                    when (uiState) {
                        is UiState.Loading -> {
                            onLoading(true)
                        }
                        is UiState.Success -> {
                            val pembayaranList = uiState.data
                            if (pembayaranList.isNullOrEmpty()) {
                                Toast.makeText(requireContext(), "Data tidak valid!", Toast.LENGTH_LONG).show()
                            } else {
                                onLoading(false)

                                rgJenisPembayaran.listenForJenisPembayaran(pembayaranList, edtTermin)
                            }
                        }
                        is UiState.Failure -> {
                            Toast.makeText(requireContext(), uiState.failMsg, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    pembayaranViewModel.insertPembayaranOperation.collect {
                        it?.also { uiState ->
                            when (uiState) {
                                is UiState.Loading -> {
                                    Snackbar.make(root, "Memproses data pembayaran ...", Snackbar.LENGTH_INDEFINITE)
                                        .show()
                                }
                                is UiState.Success -> {
                                    FormUtil.sendResultAndExit(
                                        requireActivity(), Activity.RESULT_OK, null
                                    )
                                }
                                is UiState.Failure -> {
                                    val resultIntent = Intent()
                                    resultIntent.putExtra(FormActivity.EXTRAS_FAIL_MSG, uiState.failMsg)

                                    FormUtil.sendResultAndExit(
                                        requireActivity(), Activity.RESULT_CANCELED, resultIntent,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupViewModelForEditMode() {
        with(binding) {
            pembayaranViewModel.pembayaran.observe(requireActivity()) {
                it?.also { uiState ->
                    when (uiState) {
                        is UiState.Loading -> {
                            onLoading(true)
                        }
                        is UiState.Success -> {
                            val pembayaran = uiState.data

                            if (pembayaran != null) {
                                onLoading(false)

                                rgJenisPembayaran.setCheckedJenisTermin(pembayaran.getJenisTermin())
                                disableAllJenisPembayaranRadioButtons()

                                edtTermin.setText(pembayaran.getUrutan().toString())
                                edtTermin.isEnabled = false

                                edtTanggal.setText(pembayaran.tanggal)

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

            pembayaranViewModel.ubahPembayaranOperation.observe(requireActivity()) {
                it?.also { uiState ->
                    val progressSnackbar = Snackbar.make(root, "Memproses perubahan data ..", Snackbar.LENGTH_INDEFINITE)
                    val formActivity = (requireActivity() as FormActivity)
                    val dataToSend = Intent()

                    when (uiState) {
                        is UiState.Loading -> {
                            progressSnackbar.show()
                        }
                        is UiState.Success -> {
                            dataToSend.putExtra(FormActivity.EXTRAS_SUCCESS_DATA, "Berhasil mengubah pembayaran ${pembayaranViewModel.currentTermin}!")

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

    private fun RadioGroup.listenForJenisPembayaran(
        pembayaranList: List<Pembayaran>,
        edtTermin: TextInputEditText,
    ) {
        with(binding) {
            rbItj.setOnClickListener {
                formViewModel.setSelectedJenisTermin(Pembayaran.JenisPembayaran.ITJ)
            }
            rbDp.setOnClickListener {
                formViewModel.setSelectedJenisTermin(Pembayaran.JenisPembayaran.DP)
            }
            rbTermin.setOnClickListener {
                formViewModel.setSelectedJenisTermin(Pembayaran.JenisPembayaran.TERMIN)
            }
        }

        // Listen for radio button jenis termin changes
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                formViewModel.pembayaranSelectedJenisTermin.collect {
                    withContext(Dispatchers.Main) {
                        if (it != null) {
                            val nextSequencePembayaran = Pembayaran.nextPembayaranSequence(
                                pembayaranList, it
                            )

                            edtTermin.isEnabled = true
                            edtTermin.setText(nextSequencePembayaran)
                        } else {
                            edtTermin.isEnabled = false
                        }
                    }
                }
            }
        }
    }

    private fun FragmentFormInputPembayaranKavlingBinding.onLoading(
        isLoading: Boolean,
        loadingText: String? = null
    ) {
        layoutLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        layoutContent.visibility = if (isLoading) View.GONE else View.VISIBLE

        tvLoadingMessage.text = if (!loadingText.isNullOrEmpty())
            loadingText else "Memverifikasi data pembayaran ..."
    }

    /**
     * Prevent user from changing data from either radio button or edittext
     */
    private fun FragmentFormInputPembayaranKavlingBinding.disableForms(fabAction: FloatingActionButton) {
        onLoading(true, "Memproses data ...")

        fabAction.hide()
    }
}